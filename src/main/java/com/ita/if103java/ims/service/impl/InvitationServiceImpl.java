package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.UserDtoMapper;
import com.ita.if103java.ims.service.InvitationService;
import com.ita.if103java.ims.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class InvitationServiceImpl implements InvitationService {

    private UserDao userDao;
    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;
    private MailService mailService;
    private UserDtoMapper userDtoMapper;

    @Autowired
    public InvitationServiceImpl(MailService mailService, UserDao userDao, AccountDao accountDao, AccountTypeDao accountTypeDao, UserDtoMapper userDtoMapper) {
        this.mailService = mailService;
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public void inviteUser(String email, Long accountId) {
        if (allowToInvite(accountId)) {
            User user = userDao.create(new User());
            user.setEmail(email);
            user.setRole(Role.WORKER);
            user.setAccountId(accountId);
            String emailUUID = UUID.randomUUID().toString();
            user.setEmailUUID(emailUUID);
            sendInvitationMessage(userDtoMapper.convertUserToUserDto(user), accountId, emailUUID);
        }
    }

    private void sendInvitationMessage(UserDto userDto, Long accountId, String emailUUID) {
        Account account = accountDao.findById(accountId);
        mailService.sendMessage(userDto, "Hello, We invite you to join our organization " + account.getName() + " in the Inventory Management System.\n" +
            "Please follow link bellow to proceed with registration:\n" +
            "http://localhost:8080/ims/invite/registration?uuid=" + emailUUID + "\n" +
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
