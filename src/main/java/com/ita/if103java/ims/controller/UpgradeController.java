package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/upgrade")
public class UpgradeController {
    private UpgradeService upgradeService;

    @Autowired
    public UpgradeController(UpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    @PutMapping("/{typeId}")
    public void upgrade(@AuthenticationPrincipal User user, @PathVariable("typeId") Long typeId) {
        upgradeService.upgradeAccount(user.getAccountId(), typeId);
    }
}
