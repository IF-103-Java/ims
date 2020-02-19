package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.ResetPasswordDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.UserNotFoundException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.security.SecurityInterceptor;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ita.if103java.ims.security.SecurityInterceptor.init;
import static com.ita.if103java.ims.util.DataUtil.getListOfUsers;
import static com.ita.if103java.ims.util.DataUtil.getTestUser;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private User user;
    private UserDtoMapper mapper;
    private UserDto userDto;
    private String createdDate;
    private String updatedDate;
    private ObjectMapper objectMapper;
    private UserDetailsImpl userDetails;

    private final Long fakeId = 100L;
    private final UserNotFoundException userNotFoundException = new UserNotFoundException("User with these data not found");


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mapper = new UserDtoMapper();
        userController = new UserController(userService, mapper);
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .addInterceptors(new SecurityInterceptor())
            .setCustomArgumentResolvers(
                new AuthenticationPrincipalArgumentResolver(),
                new PageableHandlerMethodArgumentResolver())
            .build();

        when(this.passwordEncoder.encode(anyString()))
            .thenReturn("$2a$10$jl.3oXcrjxo9qcCUoQIzT.TKxCNHAvlDaL3uh/ekUM.XpSa/Rhnse");

        //Initialization oftest user
        user = getTestUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDto = mapper.toDto(user);

        //Converting zoned to local
        createdDate = userDto.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        updatedDate = userDto.getUpdatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));

        //Initialization of principal
        userDetails = new UserDetailsImpl(user);
        AccountType accountType = new AccountType();
        userDetails.setAccountType(accountType);

        //Initialization of json mapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @Test
    void findById_successFlow() throws Exception {
        when(userService.findById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/" + userDto.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userDto.getId()))
            .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
            .andExpect(jsonPath("$.email").value(userDto.getEmail()))
            .andExpect(jsonPath("$.role").value(userDto.getRole().toString()))
            .andExpect(jsonPath("$.createdDate").value(createdDate))
            .andExpect(jsonPath("$.updatedDate").value(updatedDate))
            .andExpect(jsonPath("$.active").value(userDto.isActive()))
            .andExpect(jsonPath("$.accountId").value(userDto.getAccountId()));
    }

    @Test
    void findById_badFlow() throws Exception {
        when(userService.findById(fakeId)).thenThrow(userNotFoundException);

        mockMvc.perform(get("/users/" + fakeId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(userNotFoundException.getLocalizedMessage()));
    }

    @Test
    void findByEmail_successFlow() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userDto);

        mockMvc.perform(get("/users?email=" + userDto.getEmail())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userDto.getId()))
            .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
            .andExpect(jsonPath("$.email").value(userDto.getEmail()))
            .andExpect(jsonPath("$.role").value(userDto.getRole().toString()))
            .andExpect(jsonPath("$.createdDate").value(createdDate))
            .andExpect(jsonPath("$.updatedDate").value(updatedDate))
            .andExpect(jsonPath("$.active").value(userDto.isActive()))
            .andExpect(jsonPath("$.accountId").value(userDto.getAccountId()));
    }

    @Test
    void findByEmail_badFlow() throws Exception {
        String fakeEmail = "";
        when(userService.findByEmail(fakeEmail)).thenThrow(userNotFoundException);

        mockMvc.perform(get("/users?email=" + fakeEmail)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(userNotFoundException.getLocalizedMessage()));
    }


    @Test
    void findAdminByAccountId_successFlow() throws Exception {
        when(userService.findAdminByAccountId(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/account/admin")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userDto.getId()))
            .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
            .andExpect(jsonPath("$.email").value(userDto.getEmail()))
            .andExpect(jsonPath("$.role").value(userDto.getRole().toString()))
            .andExpect(jsonPath("$.createdDate").value(createdDate))
            .andExpect(jsonPath("$.updatedDate").value(updatedDate))
            .andExpect(jsonPath("$.active").value(userDto.isActive()))
            .andExpect(jsonPath("$.accountId").value(userDto.getAccountId()));
    }

    @Test
    void findAll_successFlow() throws Exception {
        // Initialization of an userDto list
        List<UserDto> users = mapper.toDtoList(getListOfUsers());

        // First 3 users (should return 1, because there is only 1 user with role WORKER and an active status in db)
        Page<UserDto> page = new PageImpl<>(Collections.singletonList(users.get(1)));
        PageRequest pageable = PageRequest.of(0, 3, Sort.Direction.ASC, "id");

        when(userService.findAll(any(PageRequest.class), anyLong())).thenReturn(page);


        mockMvc.perform(get("/users/account/users/?page=" + pageable.getPageNumber() +
            "&size=" + pageable.getPageSize() +
            "&sort=" + pageable.getSort().toString())
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.numberOfElements").value(page.getTotalElements()));
    }

    @Test
    void getCurrentUser_successFlow() throws Exception {
        mockMvc.perform(get("/users/me")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userDto.getId()))
            .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
            .andExpect(jsonPath("$.email").value(userDto.getEmail()))
            .andExpect(jsonPath("$.role").value(userDto.getRole().toString()))
            .andExpect(jsonPath("$.createdDate").value(createdDate))
            .andExpect(jsonPath("$.updatedDate").value(updatedDate))
            .andExpect(jsonPath("$.active").value(userDto.isActive()))
            .andExpect(jsonPath("$.accountId").value(userDto.getAccountId()));
    }

    @Test
    void delete_successFlow() throws Exception {
        when(userService.delete(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(delete("/users/" + userDto.getId())
            .principal(init(userDetails))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_failFlow() throws Exception {
        when(userService.delete(eq(fakeId), anyLong())).thenThrow(userNotFoundException);
        mockMvc.perform(delete("/users/" + fakeId)
            .principal(init(userDetails)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(userNotFoundException.getLocalizedMessage()));
    }

    @Test
    void activate_successFlow() throws Exception {
        when(userService.activateUser(anyString())).thenReturn(true);

        mockMvc.perform(post("/users/confirmation?emailUUID=" + userDto.getEmailUUID())
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

    @Test
    void activate_failFlow() throws Exception {
        String fakeEmailUUID = "";
        when(userService.activateUser(fakeEmailUUID)).thenThrow(userNotFoundException);

        mockMvc.perform(post("/users/confirmation?emailUUID=" + fakeEmailUUID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(userNotFoundException.getLocalizedMessage()));
    }

    @Test
    void getUserNames_successFlow() throws Exception {
        Map<Long, String> usernames = new HashMap();
        usernames.put(1l, "Mary Smith");
        usernames.put(2l, "Lucy Clack");

        when(userService.findAllUserNames(any(UserDetailsImpl.class))).thenReturn(usernames);

        mockMvc.perform(get("/users/usernames")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.1").value(usernames.get(1l)))
            .andExpect(jsonPath("$.2").value(usernames.get(2l)));
    }

    @Test
    void update_successFlow() throws Exception {
        UserDto updatedUser = new UserDto();
        updatedUser.setFirstName("newFirstName");
        updatedUser.setLastName("newLastName");

        when(userService.update(any(UserDto.class))).thenReturn(updatedUser);


        // Converting DTO object to json
        String json = objectMapper.writeValueAsString(updatedUser);


        mockMvc.perform(put("/users/me")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value(updatedUser.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(updatedUser.getLastName()));
    }

    @Test
    void updatePassword_successFlow() throws Exception {
        String newPassword = "12345678";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setCurrentPassword(userDto.getPassword());
        resetPasswordDto.setNewPassword(newPassword);

        when(userService.updatePassword(any(UserDto.class), any(ResetPasswordDto.class))).thenReturn(true);

        // Converting DTO object to json
        String json = objectMapper.writeValueAsString(resetPasswordDto);


        mockMvc.perform(put("/users/update-password")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());
    }

    @Test
    void updatePassword_badFlow() throws Exception {
        //Initialization of test data
        String newPassword = "12345678";
        String fakePassword = "";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setCurrentPassword(fakePassword);
        resetPasswordDto.setNewPassword(newPassword);

        when(userService.updatePassword(eq(userDto), eq(resetPasswordDto)))
            .thenThrow(new IllegalArgumentException());

        // Converting DTO object to json
        String json = objectMapper.writeValueAsString(resetPasswordDto);

        mockMvc.perform(put("/users/update-password")
            .principal(init(userDetails)))
            .andExpect(status().isBadRequest());
    }
}
