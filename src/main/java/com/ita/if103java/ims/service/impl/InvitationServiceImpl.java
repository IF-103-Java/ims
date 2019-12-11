package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.service.InvitationService;
import com.ita.if103java.ims.service.MailService;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class InvitationServiceImpl implements InvitationService {

    @Value("${mail.invitationURL}")
    private String invitationURL;
    private UserDao userDao;
    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;
    private MailService mailService;
    private UserService userService;

    @Autowired
    public InvitationServiceImpl(MailService mailService, UserDao userDao, AccountDao accountDao, AccountTypeDao accountTypeDao, UserService userService) {
        this.mailService = mailService;
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
        this.userService = userService;
    }

    @Override
    public void inviteUser(String email, Long accountId) {
        if (allowToInvite(accountId)) {
            UserDto userDto = new UserDto();
            userDto.setEmail(email);
            userDto.setAccountId(accountId);
            userDto.setRole(Role.WORKER);
            UserDto createdUserDto = userService.create(userDto);
            sendInvitationMessage(createdUserDto, accountId);
        }
    }

    private void sendInvitationMessage(UserDto userDto, Long accountId) {
        Account account = accountDao.findById(accountId);
        mailService.sendMessage(userDto, "Hello, We invite you to join our organization " + account.getName() + " in the Inventory Management System.\n" +
            "Please follow link bellow to proceed with registration:\n" +
            invitationURL + userDto.getEmailUUID() + "\n" +
            "If you didn't provide your email for registration, please ignore this email.\n" +
            "\n" +
            "Regards,\n" +
            "IMS team", "IMS. Invitation to " + account.getName() + " organization");
    }

    private boolean allowToInvite(Long accountId) {
        Integer usersCount = userDao.countOfUsers(accountId);
        Integer usersAllowed = accountTypeDao.findById(accountDao.findById(accountId).getTypeId()).getMaxUsers();
        return usersCount < usersAllowed;
    }
}
