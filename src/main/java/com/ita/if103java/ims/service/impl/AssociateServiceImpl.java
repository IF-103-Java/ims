package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.AssociateDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AssociateService;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssociateServiceImpl implements AssociateService {

    private AssociateDao associateDao;
    private AddressDao addressDao;
    private AssociateDtoMapper associateDtoMapper;
    private AddressDtoMapper addressDtoMapper;
    private EventService eventService;

    @Autowired
    public AssociateServiceImpl(AssociateDao associateDao, AddressDao addressDao,
                                AssociateDtoMapper associateDtoMapper, AddressDtoMapper addressDtoMapper,
                                EventService eventService) {
        this.associateDao = associateDao;
        this.addressDao = addressDao;
        this.associateDtoMapper = associateDtoMapper;
        this.addressDtoMapper = addressDtoMapper;
        this.eventService = eventService;
    }

    @Override
    public Optional<AssociateDto> create(UserDetailsImpl user, AssociateDto associateDto) {

        if (allowToCreateNewAssociate(user, associateDto.getType())) {
            Associate associate = associateDao.create(associateDtoMapper.toEntity(associateDto));
            Address address = addressDtoMapper.toEntity(associateDto.getAddressDto());
            address.setAssociateId(associate.getId());

            addressDao.createAssociateAddress(associate.getId(), address);

            EventName eventName = associate.getType() == AssociateType.SUPPLIER ? EventName.NEW_SUPPLIER : EventName.NEW_CLIENT;
            createEvent(user, associate, eventName);

            return Optional.of(associateDtoMapper.toDto(associate));
        }

        return Optional.empty();
    }

    @Override
    public AssociateDto update(UserDetailsImpl user, AssociateDto associateDto) {
        Associate associate = associateDao.update(associateDtoMapper.toEntity(associateDto));

        EventName eventName = associate.getType() == AssociateType.SUPPLIER ? EventName.SUPPLIER_EDITED : EventName.CLIENT_EDITED;
        createEvent(user, associate, eventName);

        return associateDtoMapper.toDto(associate);
    }

    @Override
    public AssociateDto view(Long id) {
        AssociateDto associateDto = associateDtoMapper.toDto(associateDao.findById(id));
        AddressDto addressDto = addressDtoMapper.toDto(addressDao.findByAssociateId(id));

        associateDto.setAddressDto(addressDto);

        return associateDto;
    }

    @Override
    public List<AssociateDto> findAll() {
        List<AssociateDto> associateDtos = associateDtoMapper.toDtoList(associateDao.findAll());

        associateDtos.stream().forEach(a -> a.setAddressDto(addressDtoMapper.toDto(addressDao.findByAssociateId(a.getId()))));

        return associateDtos;
    }

    @Override
    public boolean delete(UserDetailsImpl user, Long id) {

        Associate associate = associateDao.findById(id);

        if (associateDao.delete(id)) {
            EventName eventName = associate.getType() == AssociateType.SUPPLIER ? EventName.SUPPLIER_REMOVED : EventName.CLIENT_REMOVED;
            createEvent(user, associate, eventName);

            return true;
        }

        return false;
    }

    private void createEvent(UserDetailsImpl user, Associate associate, EventName eventName) {
        Event event = new Event();
        event.setMessage(String.format("%s with name %s", eventName.getLabel(), associate.getName()));
        event.setAccountId(user.getUser().getAccountId());
        event.setAuthorId(user.getUser().getId());
        event.setName(eventName);

        eventService.create(event);
    }

    private boolean allowToCreateNewAssociate(UserDetailsImpl user, AssociateType associateType) {

        List<Associate> associates = associateDao.findByAccountId(user.getUser().getAccountId());

        if (associateType == AssociateType.SUPPLIER) {
            return user.getAccountType().getMaxSuppliers() > associates.stream()
                .filter(a -> a.getType() == AssociateType.SUPPLIER).count();
        } else if (associateType == AssociateType.CLIENT) {
            return user.getAccountType().getMaxClients() > associates.stream()
                .filter(a -> a.getType() == AssociateType.CLIENT).count();
        }

        return false;
    }
}
