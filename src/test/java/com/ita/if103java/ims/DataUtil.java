package com.ita.if103java.ims;

import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataUtil {
    private static final ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());

    public static List<User> getListOfUsers() {
        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setId(1l);
        user1.setFirstName("Mary1");
        user1.setLastName("Smith1");
        user1.setEmail("mary1.smith1@gmail.com");
        user1.setPassword("qwerty12345");
        user1.setRole(Role.ROLE_ADMIN);
        user1.setCreatedDate(currentDateTime);
        user1.setUpdatedDate(currentDateTime);
        user1.setActive(true);
        user1.setEmailUUID(UUID.randomUUID().toString());
        user1.setAccountId(1l);

        User user2 = new User();
        user2.setId(2l);
        user2.setFirstName("Mary2");
        user2.setLastName("Smith2");
        user2.setEmail("mary2.smith2@gmail.com");
        user2.setPassword("qwerty12345");
        user2.setRole(Role.ROLE_WORKER);
        user2.setCreatedDate(currentDateTime);
        user2.setUpdatedDate(currentDateTime);
        user2.setActive(true);
        user2.setEmailUUID(UUID.randomUUID().toString());
        user2.setAccountId(1l);

        User user3 = new User();
        user3.setId(3l);
        user3.setFirstName("Mary3");
        user3.setLastName("Smith3");
        user3.setEmail("mary3.smith3@gmail.com");
        user3.setPassword("qwerty12345");
        user3.setRole(Role.ROLE_WORKER);
        user3.setCreatedDate(currentDateTime);
        user3.setUpdatedDate(currentDateTime);
        user3.setActive(false);
        user3.setEmailUUID(UUID.randomUUID().toString());
        user3.setAccountId(1l);

        users.add(user1);
        users.add(user2);
        users.add(user3);

        return users;
    }

    public static User getTestUser() {
        User user1 = new User();
        user1.setId(1l);
        user1.setFirstName("Mary");
        user1.setLastName("Smith");
        user1.setEmail("mary.smith@gmail.com");
        user1.setPassword("qwerty12345");
        user1.setRole(Role.ROLE_ADMIN);
        user1.setCreatedDate(currentDateTime);
        user1.setUpdatedDate(currentDateTime);
        user1.setActive(true);
        user1.setEmailUUID(UUID.randomUUID().toString());
        user1.setAccountId(1l);

        return user1;
    }
}
