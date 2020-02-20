package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.ForgotPasswordDto;
import com.ita.if103java.ims.dto.UserLoginDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.UserOrPasswordIncorrectException;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.security.JwtTokenProvider;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.LoginService;
import com.ita.if103java.ims.service.MailService;
import com.mysql.cj.exceptions.PasswordExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ita.if103java.ims.config.MailMessagesConfig.FOOTER;
import static com.ita.if103java.ims.config.MailMessagesConfig.RESET_PASSWORD;
import static com.ita.if103java.ims.entity.EventName.LOGIN;
import static com.ita.if103java.ims.entity.EventName.PASSWORD_CHANGED;
import static com.ita.if103java.ims.util.TokenUtil.isValidToken;
import static com.ita.if103java.ims.util.UserEventUtil.createEvent;
import static org.springframework.http.ResponseEntity.ok;

@Service
public class LoginServiceImpl implements LoginService {

    @Value("${mail.resetPasswordURL}")
    private String resetPasswordURL;

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private MailService mailService;
    private UserDtoMapper mapper;
    private EventService eventService;
    private AuthenticationManager authManager;
    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;

    @Autowired
    public LoginServiceImpl(UserDao userDao,
                            PasswordEncoder passwordEncoder,
                            JwtTokenProvider jwtTokenProvider,
                            MailService mailService,
                            UserDtoMapper mapper,
                            EventService eventService,
                            AuthenticationManager authManager,
                            AccountDao accountDao,
                            AccountTypeDao accountTypeDao) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
        this.mapper = mapper;
        this.eventService = eventService;
        this.authManager = authManager;
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
    }

    @Override
    public ResponseEntity<Map<String, String>> signIn(UserLoginDto user) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            User regUser = userDao.findByEmail(user.getUsername());
            eventService.create(createEvent(regUser, LOGIN, "sign in to account."));
            String token = jwtTokenProvider.createToken(user.getUsername());
            AccountType type = accountTypeDao.findById(accountDao.findById(regUser.getAccountId()).getTypeId());

            String username = new StringBuilder()
                .append(regUser.getFirstName())
                .append(" ")
                .append(regUser.getLastName()).toString();
            Map<String, String> model = new HashMap<>();
            model.put("token", token);
            model.put("username", username);
            model.put("accountId", regUser.getAccountId().toString());
            model.put("accountType", type.getId().toString());
            model.put("role", regUser.getRole().toString());
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e) {
            throw new UserOrPasswordIncorrectException("Credentials aren't correct", e);
        }

    }


    @Override
    public void sendResetPasswordToken(ForgotPasswordDto forgotPasswordDto) {
        String newToken = UUID.randomUUID().toString();

        User user = userDao.findByEmail(forgotPasswordDto.getEmail());
        user.setEmailUUID(newToken);
        userDao.update(user);

        String message = "" +
            RESET_PASSWORD +
            resetPasswordURL + newToken + "\n" +
            FOOTER;
        mailService.sendMessage(mapper.toDto(user), message, "RESET PASSWORD");
    }

    @Override
    public void resetPassword(String emailUUID, String newPassword) {
        User user = userDao.findByEmailUUID(emailUUID);
        if (isValidToken(user)) {
            String newEncodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(newEncodedPassword);
            userDao.updatePassword(user.getId(), newEncodedPassword);
            eventService.create(createEvent(user, PASSWORD_CHANGED, "reset the password."));
        } else {
            throw new PasswordExpiredException("Expired time of token isn't valid");
        }

    }

}
