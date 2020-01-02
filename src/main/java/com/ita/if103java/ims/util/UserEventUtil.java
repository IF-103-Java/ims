package com.ita.if103java.ims.util;

import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;

public class UserEventUtil {

    public static Event createEvent(User user, EventName name, String message) {
        String messageHeader = "User: " + user.getFirstName() + " " + user.getLastName() + " ";

        Event event = new Event();
        event.setMessage(messageHeader + message);
        event.setAccountId(user.getAccountId());
        event.setAuthorId(user.getId());
        event.setName(name);

        return event;
    }
}
