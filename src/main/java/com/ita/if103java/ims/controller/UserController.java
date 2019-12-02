package com.ita.if103java.ims.controller;


import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.dto.transfer.ExistData;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.UserDtoMapper;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private UserDtoMapper userDtoMapper;

    @Autowired
    public UserController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @GetMapping(value = "/get/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findByEmail(@RequestParam("email") String email) {
        return userService.findByEmail(email);
    }

    @GetMapping(value = "/{accountId}/get-users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findByAccountId(@PathVariable("accountId") Long accountId) {
        return userService.findByAccountId(accountId);
    }

    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@AuthenticationPrincipal User user, @Validated({ExistData.class}) @RequestBody UserDto userDto) {
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole());

        return userService.update(userDto);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/all")
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping(value = "/dashboards/{emailUUID}")
    @ResponseStatus(HttpStatus.OK)
    public boolean activateUser(@PathVariable("emailUUID") String emailUUID) {
        return userService.activateUser(emailUUID);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@AuthenticationPrincipal User user,
                              @Validated @RequestBody @NotNull String newPassword) {
        userService.updatePassword(user.getId(), newPassword);
    }

    @GetMapping("/current-user")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user) {
        return userDtoMapper.convertUserToUserDto(user);
    }

}
