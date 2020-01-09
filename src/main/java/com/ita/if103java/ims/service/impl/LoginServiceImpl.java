package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserLoginDto;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ita.if103java.ims.config.MailMessagesConfig.FOOTER;
import static com.ita.if103java.ims.config.MailMessagesConfig.RESET_PASSWORD;
import static com.ita.if103java.ims.entity.EventName.PASSWORD_CHANGED;
import static com.ita.if103java.ims.entity.EventName.LOGIN;
import static com.ita.if103java.ims.util.TokenUtil.isValidToken;
import static com.ita.if103java.ims.util.UserEventUtil.createEvent;

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

    @Autowired
    public LoginServiceImpl(UserDao userDao,
                            PasswordEncoder passwordEncoder,
                            JwtTokenProvider jwtTokenProvider,
                            MailService mailService,
                            UserDtoMapper mapper,
                            EventService eventService,
                            AuthenticationManager authManager) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
        this.mapper = mapper;
        this.eventService = eventService;
        this.authManager = authManager;
    }

    @Override
    public String signIn(UserLoginDto user) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            User regUser = userDao.findByEmail(user.getUsername());
            eventService.create(createEvent(regUser, LOGIN , "sign in to account."));
            return jwtTokenProvider.createToken(user.getUsername());
        } catch (AuthenticationException e) {
            throw new UserOrPasswordIncorrectException("Credential aren't correct", e);
        }

    }


    @Override
    public void sendResetPasswordToken(String email) {
        String newToken = UUID.randomUUID().toString();

        User user = userDao.findByEmail(email);
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
            eventService.create(createEvent(user, PASSWORD_CHANGED , "reset the password."));
        } else {
            throw new PasswordExpiredException("Expired time of token isn't valid");
        }

    }

}
