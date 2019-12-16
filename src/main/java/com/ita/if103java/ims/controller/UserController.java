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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private UserDtoMapper mapper;

    @Autowired
    public UserController(UserService userService, UserDtoMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findByEmail(@RequestParam("email") String email) {
        return userService.findByEmail(email);
    }

    @GetMapping(value = "/account/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findUsersByAccountId(@RequestParam("accountId") Long accountId) {
        return userService.findUsersByAccountId(accountId);
    }

    @GetMapping(value = "/account/admin")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findAdminByAccountId(@RequestParam("accountId") Long accountId) {
        return userService.findAdminByAccountId(accountId);
    }

    @PutMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@AuthenticationPrincipal User user, @Validated({ExistData.class}) @RequestBody UserDto userDto) {
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole());

        return userService.update(userDto);
    }

    //TODO: add @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping(value = "/confirmation")
    @ResponseStatus(HttpStatus.OK)
    public boolean activateUser(@RequestParam("emailUUID") String emailUUID) {
        return userService.activateUser(emailUUID);
    }

    @PostMapping("/update-password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@AuthenticationPrincipal User user,
                               @Validated({ExistData.class}) @RequestBody @NotNull String newPassword) {
        userService.updatePassword(user.getId(), newPassword);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user) {
        return mapper.toDto(user);
    }

}
