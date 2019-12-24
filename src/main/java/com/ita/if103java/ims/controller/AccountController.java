package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(value = "/")
    public AccountDto create(@AuthenticationPrincipal User user, @RequestBody AccountDto accountDto) {
        return accountService.create(user, accountDto);
    }

    @PutMapping(value = "/")
    public AccountDto update(@AuthenticationPrincipal User user, @RequestBody AccountDto accountDto) {
        return accountService.update(user, accountDto);
    }

    @GetMapping(value = "/")
    public AccountDto view(@AuthenticationPrincipal User user) {
        return accountService.view(user.getAccountId());
    }

    @DeleteMapping(value = "/")
    public void delete(@AuthenticationPrincipal User user) {
        accountService.delete(user);
    }

}
