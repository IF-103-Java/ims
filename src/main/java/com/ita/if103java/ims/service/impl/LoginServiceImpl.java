package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserLoginDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.UserDtoMapper;
import com.ita.if103java.ims.security.JwtTokenProvider;
import com.ita.if103java.ims.service.LoginService;
import com.ita.if103java.ims.service.MailService;
import com.mysql.cj.exceptions.PasswordExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.ita.if103java.ims.config.MailMessagesConfig.FOOTER;
import static com.ita.if103java.ims.config.MailMessagesConfig.RESET_PASSWORD;
import static com.ita.if103java.ims.util.TokenUtil.isValidToken;

@Service
public class LoginServiceImpl implements LoginService {

    @Value("${mail.resetPasswordURL}")
    private String resetPasswordURL;

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private MailService mailService;
    private UserDtoMapper mapper;

    @Autowired
    public LoginServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                            MailService mailService, UserDtoMapper mapper) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
        this.mapper = mapper;
    }

    @Override
    public String signIn(UserLoginDto userLoginDto) {
        User user = userDao.findByEmail(userLoginDto.getUsername());
        if (passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            return jwtTokenProvider.createToken(userLoginDto.getUsername(),
                userDao.findByEmail(userLoginDto.getUsername()).getRole());
        } else {
            throw new IllegalArgumentException("Password isn't correct");
        }
    }

    @Override
    public void sendResetPasswordToken(String email) {
        String newToken = UUID.randomUUID().toString();

        User user = userDao.findByEmail(email);
        user.setUpdatedDate(ZonedDateTime.now(ZoneId.systemDefault()));
        user.setEmailUUID(newToken);
        userDao.update(user);

        String token = user.getEmailUUID();
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
        } else {
            throw new PasswordExpiredException("Expired time of token isn't valid");
        }

    }

}
