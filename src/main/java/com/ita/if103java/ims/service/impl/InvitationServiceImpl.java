package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.UserLimitReachedException;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.InvitationService;
import com.ita.if103java.ims.service.MailService;
import com.ita.if103java.ims.service.UserService;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ita.if103java.ims.config.MailMessagesConfig.INVITE_START;
import static com.ita.if103java.ims.config.MailMessagesConfig.INVITE_MIDDLE;
import static com.ita.if103java.ims.config.MailMessagesConfig.INVITE_FOOTER;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Value("${mail.activationURL}")
    private String activationURL;
    private UserDao userDao;
    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;
    private MailService mailService;
    private UserService userService;
    private EventService eventService;

    @Autowired
    public InvitationServiceImpl(MailService mailService, UserDao userDao, AccountDao accountDao, AccountTypeDao accountTypeDao, UserService userService, EventService eventService) {
        this.mailService = mailService;
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public void inviteUser(User accountAdmin, UserDto userDto) {
        if (allowToInvite(accountAdmin.getAccountId())) {
            userDto.setPassword(generatePassword());
            userDto.setRole(Role.ROLE_WORKER);
            userDto.setAccountId(accountAdmin.getAccountId());
            UserDto createdUserDto = userService.create(userDto);
            sendInvitationMessage(createdUserDto, accountAdmin.getAccountId());

            Event event = new Event("New worker " + createdUserDto.getFirstName() + " " + createdUserDto.getLastName() + " was invited.",
                accountAdmin.getAccountId(), null,
                accountAdmin.getId(), EventName.WORKER_INVITED, null);
            eventService.create(event);
        } else throw new UserLimitReachedException("The maximum users for account has been reached.");
    }

    private void sendInvitationMessage(UserDto userDto, Long accountId) {
        Account account = accountDao.findById(accountId);
        mailService.sendMessage(userDto,  INVITE_START + account.getName() + INVITE_MIDDLE +
            activationURL + userDto.getEmailUUID() + "\n" +
            "Your password: " + userDto.getPassword() + "\n" +
            INVITE_FOOTER, "IMS. Invitation to " + account.getName() + " organization");
    }

    private boolean allowToInvite(Long accountId) {
        Integer usersCount = userDao.countOfUsers(accountId);
        Integer usersAllowed = accountTypeDao.findById(accountDao.findById(accountId).getTypeId()).getMaxUsers();
        return usersCount < usersAllowed;
    }

    private String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, lowerCaseRule,
            upperCaseRule, digitRule);
    }
}
