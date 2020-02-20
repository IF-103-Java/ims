package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.ForgotPasswordDto;
import com.ita.if103java.ims.dto.UserLoginDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.UserNotFoundException;
import com.ita.if103java.ims.exception.service.UserOrPasswordIncorrectException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.service.LoginService;
import com.mysql.cj.exceptions.PasswordExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.HashMap;

import static com.ita.if103java.ims.util.DataUtil.getTestUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class LoginControllerTest {

    @Mock
    private LoginService loginService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;
    private User user;
    private ObjectMapper objectMapper;

    private final UserNotFoundException userNotFoundException
        = new UserNotFoundException("User with these data not found");
    private final PasswordExpiredException passwordExpiredException
        = new PasswordExpiredException("Expired time of token isn't valid");
    private final UserOrPasswordIncorrectException userOrPasswordIncorrectException =
        new UserOrPasswordIncorrectException("Credentials aren't correct");


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(loginController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();

        when(this.passwordEncoder.encode(anyString()))
            .thenReturn("$2a$10$jl.3oXcrjxo9qcCUoQIzT.TKxCNHAvlDaL3uh/ekUM.XpSa/Rhnse");

        //Initialization of json mapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        //Initialization of test user
        user = getTestUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }


    @Test
    void signIn_successFlow() throws Exception {

        //Initialization of test loginDto
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(user.getEmail());
        userLoginDto.setPassword(user.getPassword());

        String json = objectMapper.writeValueAsString(userLoginDto);

        when(loginService.signIn(any(UserLoginDto.class))).thenReturn(ResponseEntity.ok(new HashMap<>()));

        mockMvc.perform(post("/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());
    }

    @Test
    void signIn_badFlow() throws Exception {
        //Initialization of fake loginDto
        UserLoginDto fakeLoginDto = new UserLoginDto();
        fakeLoginDto.setUsername(user.getEmail());
        fakeLoginDto.setPassword("fakePassword");

        String json = objectMapper.writeValueAsString(fakeLoginDto);

        when(loginService.signIn(fakeLoginDto)).thenThrow(userOrPasswordIncorrectException);

        mockMvc.perform(post("/sign-in/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("message").value(userOrPasswordIncorrectException.getLocalizedMessage()));
    }


    @Test
    void forgotPassword_successFlow() throws Exception {
        //Initialization of test forgotPasswordDto
        ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setEmail(user.getEmail());
        String json = objectMapper.writeValueAsString(forgotPasswordDto);

        mockMvc.perform(post("/forgot-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());

        verify(loginService).sendResetPasswordToken(any(ForgotPasswordDto.class));
    }

    @Test
    void forgotPassword_badFlow() throws Exception {
        //Initialization of fake forgotPasswordDto
        ForgotPasswordDto fakePasswordDto = new ForgotPasswordDto();
        fakePasswordDto.setEmail("fakeEmail@gmail.com");
        String json = objectMapper.writeValueAsString(fakePasswordDto);

        doThrow(userNotFoundException).when(loginService).sendResetPasswordToken(fakePasswordDto);

        mockMvc.perform(post("/forgot-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(userNotFoundException.getLocalizedMessage()));
    }

    @Test
    void resetPassword_successFlow() throws Exception {
        String newPassword = "12345678";
        String json = objectMapper.writeValueAsString(newPassword);

        mockMvc.perform(post("/reset-password?emailUUID=" + user.getEmailUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());

        verify(loginService).resetPassword(anyString(), anyString());
    }

    @Test
    void resetPassword_badFlow() throws Exception {
        String newPassword = "12345678";
        String fakeToken = user.getEmailUUID();
        String json = objectMapper.writeValueAsString(newPassword);

        doThrow(passwordExpiredException).when(loginService).resetPassword(eq(fakeToken), anyString());

        mockMvc.perform(post("/reset-password?emailUUID=" + fakeToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value(passwordExpiredException.getLocalizedMessage()));

    }

}
