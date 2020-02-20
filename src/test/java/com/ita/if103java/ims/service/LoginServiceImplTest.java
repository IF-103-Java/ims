package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.ForgotPasswordDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.dto.UserLoginDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.UserOrPasswordIncorrectException;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.security.JwtTokenProvider;
import com.ita.if103java.ims.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.ita.if103java.ims.util.DataUtil.getTestUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginServiceImplTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private MailService mailService;
    @Mock
    private EventService eventService;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private AccountDao accountDao;
    @Mock
    private AccountTypeDao accountTypeDao;

    @InjectMocks
    private LoginServiceImpl loginService;

    private User user;
    private UserDtoMapper mapper;
    private UserLoginDto userLoginDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mapper = new UserDtoMapper();
        loginService = new LoginServiceImpl(
            userDao,
            passwordEncoder,
            jwtTokenProvider,
            mailService,
            mapper,
            eventService,
            authManager,
            accountDao,
            accountTypeDao
        );

        when(this.passwordEncoder.encode(anyString()))
            .thenReturn("$2a$10$jl.3oXcrjxo9qcCUoQIzT.TKxCNHAvlDaL3uh/ekUM.XpSa/Rhnse");

        //Initializing test user
        user = getTestUser();

        //Initializing test loginDto
        userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(user.getFirstName());
        userLoginDto.setPassword(user.getPassword());
    }

    @Test
    void sign_in_successFlow() {
        String token = """
            eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzbG9ib2RqYW5tYXJpamExKzI0QGdtYWlsLmNvbSIs
            InJvbGUiOlt7ImF1dGhvcml0eSI6IlJPTEVfQURNSU4ifV0sImlhdCI6MTU4MDc0MzQ3NSwiZ
            XhwIjoxNTgwNzc5NDc1fQ.fJfnVFN9Q1xrZpyNxFIGmJWyEGGv1_Gs4BPQ_UN9kJY
            """;

        //Initializing test account
        Account account = new Account();
        account.setTypeId(1l);

        //Initializing test accountType
        AccountType accountType = new AccountType();
        accountType.setId(1l);

        //Initializing test authentication
        authentication =
            new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword());


        when(authManager.authenticate(authentication)).thenReturn(authentication);
        when(userDao.findByEmail(userLoginDto.getUsername())).thenReturn(user);
        when(jwtTokenProvider.createToken(userLoginDto.getUsername())).thenReturn(token);

        when(accountDao.findById(anyLong())).thenReturn(account);
        when(accountTypeDao.findById(anyLong())).thenReturn(accountType);

        loginService.signIn(userLoginDto);

        verify(eventService).create(any(Event.class));

    }

    @Test
    void sign_in_authenticationException() {
        authentication = new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), "badPassword");
        when(authManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException(""));

        assertThrows(UserOrPasswordIncorrectException.class, () -> loginService.signIn(userLoginDto));
    }


    @Test
    void sendResetPasswordToken_successFlow() {
        String email = user.getEmail();
        ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setEmail(email);

        when(userDao.findByEmail(email)).thenReturn(user);

        loginService.sendResetPasswordToken(forgotPasswordDto);

        //Checking if methods were called
        verify(userDao).update(user);
        verify(mailService).sendMessage(any(UserDto.class), anyString(), anyString());
    }

    @Test
    void resetPassword_successFlow() {
        String newPassword = "12345678";
        when(userDao.findByEmailUUID(user.getEmailUUID())).thenReturn(user);

        loginService.resetPassword(user.getEmailUUID(), newPassword);

        //Checking if methods were called
        verify(userDao).updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        verify(eventService).create(any(Event.class));
    }

}
