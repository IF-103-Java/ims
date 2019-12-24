package com.ita.if103java.ims.util;

import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ItemEventUtil {

    public static Event createEvent(SavedItemDto savedItemDto, UserDto user, EventName name) {
        Event event = new Event();
        event.setAccountId(user.getAccountId());
        event.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        event.setAuthorId(user.getId());
        event.setWarehouseId(savedItemDto.getWarehouseId());
        event.setName(name);
        return event;
    }
}
