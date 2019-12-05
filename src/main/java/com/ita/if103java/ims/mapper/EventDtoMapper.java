package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventDtoMapper {

    public Event convertEventDtoToEvent(EventDto eventDto) {
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
        event.setType(eventDto.getType());
        event.setMessage(eventDto.getMessage());
        return event;
    }

    public EventDto convertEventToEventDto(Event event) {
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
        eventDto.setType(event.getType());
        eventDto.setMessage(event.getMessage());
        return eventDto;
    }

    public List<Event> convertToEventList(List<EventDto> eventDtoList) {
        return Optional.ofNullable(eventDtoList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertEventDtoToEvent)
            .collect(Collectors.toList());
    }

    public List<EventDto> convertToEventDtoList(List<Event> eventList) {
        return Optional.ofNullable(eventList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertEventToEventDto)
            .collect(Collectors.toList());
    }
}
