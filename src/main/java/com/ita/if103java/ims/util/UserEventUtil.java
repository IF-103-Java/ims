package com.ita.if103java.ims.util;

import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UserEventUtil {

    public static Event createEvent(User user, EventName name) {
        Event event = new Event();
        event.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        event.setAccountId(user.getAccountId());
        event.setAuthorId(user.getId());
        event.setName(name);

        return event;
    }
}
