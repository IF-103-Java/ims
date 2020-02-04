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
import com.ita.if103java.ims.exception.service.AssociateLimitReachedException;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.AssociateDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AssociateService;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ita.if103java.ims.dto.warehouse.advice.Address.Geo;

@Service
public class AssociateServiceImpl implements AssociateService {

    private AssociateDao associateDao;
    private AddressDao addressDao;
    private AssociateDtoMapper associateDtoMapper;
    private AddressDtoMapper addressDtoMapper;
    private EventService eventService;
    private LocationService locationService;

    @Autowired
    public AssociateServiceImpl(AssociateDao associateDao, AddressDao addressDao,
                                AssociateDtoMapper associateDtoMapper, AddressDtoMapper addressDtoMapper,
                                EventService eventService, LocationService locationService) {
        this.associateDao = associateDao;
        this.addressDao = addressDao;
        this.associateDtoMapper = associateDtoMapper;
        this.addressDtoMapper = addressDtoMapper;
        this.eventService = eventService;
        this.locationService = locationService;
    }

    @Override
    @Transactional
    public AssociateDto create(UserDetailsImpl user, AssociateDto associateDto) {

        if (allowToCreateNewAssociate(user, associateDto.getType())) {
            Associate associate = associateDao.create(user.getUser().getAccountId(),
                associateDtoMapper.toEntity(associateDto));

            Address address = addressDtoMapper.toEntity(associateDto.getAddressDto());
            address.setAssociateId(associate.getId());

            Geo geo = locationService.getLocationByAddress(address.getAddress() + " " + address.getCity() + " " +
                address.getCountry() + " " + address.getZip());

            address.setLatitude(geo.getLatitude());
            address.setLongitude(geo.getLongitude());

            address = addressDao.createAssociateAddress(associate.getId(), address);

            associateDto = associateDtoMapper.toDto(associate);
            associateDto.setAddressDto(addressDtoMapper.toDto(address));
            associateDto.setAccountId(user.getUser().getAccountId());

            EventName eventName = associate.getType() == AssociateType.SUPPLIER ? EventName.NEW_SUPPLIER : EventName.NEW_CLIENT;
            createEvent(user, associate, eventName);

            return associateDto;
        } else
            throw new AssociateLimitReachedException("The maximum of " + associateDto.getType() + "s for account has been reached.");
    }

    @Override
    @Transactional
    public AssociateDto update(UserDetailsImpl user, AssociateDto associateDto) {
        Associate associate = associateDao.update(user.getUser().getAccountId(), associateDtoMapper.toEntity(associateDto));

        Address address = addressDao.updateAssociateAddress(associate.getId(), addressDtoMapper.toEntity(associateDto.getAddressDto()));
        associateDto = associateDtoMapper.toDto(associate);
        associateDto.setAddressDto(addressDtoMapper.toDto(address));
        associateDto.setAccountId(user.getUser().getAccountId());

        EventName eventName = associate.getType() == AssociateType.SUPPLIER ? EventName.SUPPLIER_EDITED : EventName.CLIENT_EDITED;
        createEvent(user, associate, eventName);

        return associateDto;
    }

    @Override
    public AssociateDto view(UserDetailsImpl user, Long id) {
        AssociateDto associateDto = associateDtoMapper.toDto(associateDao.findById(user.getUser().getAccountId(), id));
        AddressDto addressDto = addressDtoMapper.toDto(addressDao.findByAssociateId(id));

        associateDto.setAddressDto(addressDto);

        return associateDto;
    }

    @Override
    public Page<AssociateDto> findSortedAssociates(Pageable pageable, UserDetailsImpl user) {
        Page<Associate> page = associateDao.getAssociates(pageable, user.getUser().getAccountId());
        List<AssociateDto> associateDtos = associateDtoMapper.toDtoList(page.getContent());

        associateDtos.stream().forEach(a -> a.setAddressDto(addressDtoMapper.toDto(addressDao.findByAssociateId(a.getId()))));

        return new PageImpl<>(associateDtos, pageable, page.getTotalElements());
    }

    @Override
    public boolean delete(UserDetailsImpl user, Long id) {

        Associate associate = associateDao.findById(user.getUser().getAccountId(), id);

        if (associateDao.delete(user.getUser().getAccountId(), id)) {
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

    private String checkSort(String... sort) {
        String direction = sort[1].equalsIgnoreCase("desc") ? "desc" : "asc";
        return Stream.of("id", "name", "type").
            filter(x -> x.equalsIgnoreCase(sort[0])).collect(Collectors.joining()) + " " + direction;
    }
}
