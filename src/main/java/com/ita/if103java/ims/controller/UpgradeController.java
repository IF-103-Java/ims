package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/upgrade")
public class UpgradeController {
    private UpgradeService upgradeService;

    @Autowired
    public UpgradeController(UpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    @PutMapping("/{id}")
    public void upgrade(@PathVariable("id") Long accountId, @RequestBody Long accountTypeId) {
        upgradeService.upgradeAccount(accountId, accountTypeId);
    }
}
