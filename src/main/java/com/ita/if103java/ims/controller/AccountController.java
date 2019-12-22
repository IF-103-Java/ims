package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(value = "/{id}")
    public AccountDto view(@PathVariable("id") Long id) {
        return accountService.view(id);
    }

    @PostMapping(value = "/")
    public AccountDto create(@RequestBody AccountDto accountDto) {
        return accountService.create(accountDto);
    }

    @PutMapping("/{id}")
    public AccountDto update(@RequestBody AccountDto accountDto) {
        return accountService.update(accountDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        accountService.delete(id);
    }

}
