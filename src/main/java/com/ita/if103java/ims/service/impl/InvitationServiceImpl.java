package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.service.InvitationService;
import com.ita.if103java.ims.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;

public class InvitationServiceImpl implements InvitationService {

    private UserDao userDao;
    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;
    private MailService mailService;

    @Autowired
    public InvitationServiceImpl(MailService mailService, UserDao userDao, AccountDao accountDao, AccountTypeDao accountTypeDao) {
        this.mailService = mailService;
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
    }

    @Override
    public void inviteUser(String email, Long accountId, String message) {
        userDao.create(new User(null, null, null, email, null, Role.WORKER, null, null, false, null, accountId));
        if (allowToInvite(accountId))
            sendInvitationMessage(email, message);
    }

    private void sendInvitationMessage(String email, String message) {
        mailService.sendInvitationMessage(email, "Hello, I invite you to join our Inventory Management System." + message, "Invitation");
    }

    private boolean allowToInvite(Long accountId) {
        Integer usersCount = accountDao.countOfUsers(accountId);
        Integer usersAllowed = accountTypeDao.findById(accountDao.findById(accountId).getTypeId()).getMaxUsers();
        return usersCount < usersAllowed;
    }
}
