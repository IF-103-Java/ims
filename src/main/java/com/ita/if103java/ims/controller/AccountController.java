package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/")
    public AccountDto update(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody AccountDto accountDto) {
        return accountService.update(user.getUser(), accountDto);
    }

    @GetMapping(value = "/")
    public AccountDto view(@AuthenticationPrincipal UserDetailsImpl user) {
        return accountService.view(user.getUser().getAccountId());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/")
    public void delete(@AuthenticationPrincipal UserDetailsImpl user) {
        accountService.delete(user.getUser());
    }

}
