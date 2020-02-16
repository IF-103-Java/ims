package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.UserLimitReachedException;
import com.ita.if103java.ims.service.impl.InvitationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvitationServiceImplTest {

    @Mock
    UserService userService;

    @Mock
    EventService eventService;

    @Mock
    AccountDao accountDao;

    @Mock
    MailService mailService;

    @InjectMocks
    private InvitationServiceImpl invitationService;

    @Value("${mail.activationURL}")
    private String activationURL;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void inviteUserSuccess() {
        User accountAdmin = new User();
        accountAdmin.setAccountId(1L);
        accountAdmin.setId(1L);
        UserDto userDto = new UserDto();
        userDto.setEmail("im.user@gmail.com");
        userDto.setFirstName("User");
        userDto.setLastName("User");
        UserDto createdUserDto = new UserDto();
        createdUserDto.setId(1L);
        createdUserDto.setEmail("im.user@gmail.com");
        createdUserDto.setFirstName("User");
        createdUserDto.setLastName("User");
        createdUserDto.setEmailUUID("EmailUUID");

        Account account = new Account(1L, "Account Name", 1L,
            ZonedDateTime.now(ZoneId.systemDefault()), true);
        Event event = new Event("New worker " + createdUserDto.getFirstName() + " " + createdUserDto.getLastName() + " was invited.",
            accountAdmin.getAccountId(), null,
            accountAdmin.getId(), EventName.WORKER_INVITED, null);

        when(userService.isAllowedToInvite(accountAdmin.getAccountId())).thenReturn(true);
        when(userService.create(userDto)).thenReturn(createdUserDto);
        when(accountDao.findById(accountAdmin.getAccountId())).thenReturn(account);

        invitationService.inviteUser(accountAdmin, userDto);

        verify(userService, times(1)).isAllowedToInvite(anyLong());
        verify(userService, times(1)).create(userDto);
        verify(accountDao, times(1)).findById(anyLong());
        verify(mailService, times(1)).sendMessage(ArgumentMatchers.any(), anyString(), anyString());
        verify(eventService, times(1)).create(event);
    }

    @Test
    public void inviteUserFail() {
        User accountAdmin = new User();
        accountAdmin.setAccountId(1L);
        accountAdmin.setId(1L);
        UserDto userDto = new UserDto();
        userDto.setEmail("im.user@gmail.com");
        userDto.setFirstName("User");
        userDto.setLastName("User");

        when(userService.isAllowedToInvite(accountAdmin.getAccountId())).thenReturn(false);

        assertThrows(UserLimitReachedException.class, () -> invitationService.inviteUser(accountAdmin, userDto));

        verify(userService, times(1)).isAllowedToInvite(anyLong());
        verify(userService, times(0)).create(userDto);
        verify(accountDao, times(0)).findById(anyLong());
        verify(mailService, times(0)).sendMessage(ArgumentMatchers.any(), anyString(), anyString());
        verify(eventService, times(0)).create(ArgumentMatchers.any());
    }
}
