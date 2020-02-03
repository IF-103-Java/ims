package com.ita.if103java.ims.controller;


import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.dto.transfer.ExistData;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private UserDtoMapper mapper;

    @Autowired
    public UserController(UserService userService,
                          UserDtoMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostAuthorize("returnObject.accountId == authentication.principal.getUser().accountId")
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findByEmail(@RequestParam("email") String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/account/workers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findWorkersByAccountId(@AuthenticationPrincipal UserDetailsImpl user) {
        return userService.findWorkersByAccountId(user.getUser().getAccountId());
    }

    @GetMapping("/account/admin")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findAdminByAccountId(@AuthenticationPrincipal UserDetailsImpl user) {
        return userService.findAdminByAccountId(user.getUser().getAccountId());
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@AuthenticationPrincipal UserDetailsImpl user,
                          @Validated({ExistData.class}) @RequestBody UserDto userDto) {
        userDto.setId(user.getUser().getId());
        return userService.update(userDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean delete(@PathVariable("id") Long id,
                          @AuthenticationPrincipal UserDetailsImpl user) {
        return userService.delete(id, user.getUser().getAccountId());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/account/users")
    public Page<UserDto> findAll(Pageable pageable,
                                 @AuthenticationPrincipal UserDetailsImpl user) {
        return userService.findAll(pageable, user.getUser().getAccountId());
    }

    @GetMapping("/confirmation")
    @ResponseStatus(HttpStatus.OK)
    public boolean activateUser(@RequestParam("emailUUID") String emailUUID) {
        return userService.activateUser(emailUUID);
    }

    @PostMapping("/update-password")
    @ResponseStatus(HttpStatus.OK)
    public boolean updatePassword(@AuthenticationPrincipal UserDetailsImpl user,
                                  @Validated({ExistData.class}) @RequestBody @NotNull String newPassword) {
        return userService.updatePassword(user.getUser().getId(), newPassword);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetailsImpl user) {
        return mapper.toDto(user.getUser());
    }

    @GetMapping("/usernames")
    public Map<Long, String> getUserNames(@AuthenticationPrincipal UserDetailsImpl user) {
        return userService.findAllUserNames(user);
    }

}
