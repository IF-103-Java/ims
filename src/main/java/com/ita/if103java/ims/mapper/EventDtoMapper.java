package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventDtoMapper extends AbstractEntityDtoMapper<Event, EventDto> {

    @Override
    public Event toEntity(EventDto eventDto) {
        if (eventDto == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setAccountId(eventDto.getAccountId());
        event.setAuthorId(eventDto.getAuthorId());
        event.setWarehouseId(eventDto.getWarehouseId());
        event.setTransactionId(eventDto.getTransactionId());
        event.setDate(eventDto.getDate());
        event.setName(eventDto.getName());
        event.setMessage(eventDto.getMessage());
        event.setNotification(eventDto.isNotification());
        return event;
    }

    @Override
    public EventDto toDto(Event event) {
        if (event == null) {
            return null;
        }
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setAccountId(event.getAccountId());
        eventDto.setAuthorId(event.getAuthorId());
        eventDto.setWarehouseId(event.getWarehouseId());
        eventDto.setTransactionId(event.getTransactionId());
        eventDto.setDate(event.getDate());
        eventDto.setName(event.getName());
        eventDto.setMessage(event.getMessage());
        eventDto.setNotification(event.isNotification());
        return eventDto;
    }
}
