package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.AssociateLimitReachedException;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.AssociateDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ita.if103java.ims.dto.warehouse.advice.Address.Geo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssociateServiceImplTest {

    @Mock
    private AssociateDao associateDao;

    @Mock
    private AddressDao addressDao;

    @Mock
    private EventService eventService;

    @Mock
    private LocationService locationService;

    private AssociateDtoMapper associateDtoMapper = new AssociateDtoMapper();
    private AddressDtoMapper addressDtoMapper = new AddressDtoMapper();

    @InjectMocks
    private AssociateServiceImpl associateService;

    private Associate associate = new Associate(1L, 1L, "Associate name", "test@test.com",
        "+380977959707", "additionalInfo", AssociateType.SUPPLIER, true);

    private Address address = new Address("country", "city", "address", "zip", 10F, -30F);

    private AssociateDto associateDto = associateDtoMapper.toDto(associate);

    private AddressDto addressDto = addressDtoMapper.toDto(address);

    private UserDetailsImpl userDetails;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        associateService = new AssociateServiceImpl(associateDao, addressDao,
            associateDtoMapper, addressDtoMapper,
            eventService, locationService);

        AccountType accountType = new AccountType();
        accountType.setMaxSuppliers(1);
        accountType.setMaxClients(1);

        User user = new User();
        user.setAccountId(1L);
        this.userDetails = new UserDetailsImpl(user, accountType);
    }

    @Test
    void create() {
        associateDto.setAddressDto(addressDto);

        doReturn(associate).when(associateDao).create(userDetails.getUser().getAccountId(), associate);
        when(locationService.getLocationByAddress(anyString())).thenReturn(new com.ita.if103java.ims.dto.warehouse.advice.Address.Geo(1F, 1F));

        AssociateDto result = associateService.create(userDetails, associateDto);

        verify(associateDao, times(1)).create(userDetails.getUser().getAccountId(),
            associate);
        verify(locationService, times(1)).getLocationByAddress(anyString());
        verify(addressDao, times(1)).createAssociateAddress(anyLong(), any(Address.class));
        assertEquals(associateDtoMapper.toDto(associate), result);
    }

    @Test
    void createThrowsExceptionWhenSupplierLimitReached() {
        List<Associate> associateList = Collections.singletonList(associate);

        // logic for mock
        doReturn(associateList).when(associateDao).findByAccountId(userDetails.getUser().getAccountId());

        // execute method
        assertThrows(AssociateLimitReachedException.class,
            () -> {associateService.create(userDetails, associateDto);});
    }

    @Test
    void createThrowsExceptionWhenClientLimitReached() {
        associate.setType(AssociateType.CLIENT);
        associateDto = associateDtoMapper.toDto(associate);

        List<Associate> associateList = Collections.singletonList(associate);

        // logic for mock
        doReturn(associateList).when(associateDao).findByAccountId(userDetails.getUser().getAccountId());

        // execute method
        assertThrows(AssociateLimitReachedException.class,
            () -> {associateService.create(userDetails, associateDto);});
    }


    @Test
    void view() {
        doReturn(associate).when(associateDao).findById(userDetails.getUser().getAccountId(), associateDto.getId());
        doReturn(address).when(addressDao).findByAssociateId(associateDto.getId());

        AssociateDto result = associateService.view(userDetails, associateDto.getId());

        assertThat(result).isEqualToIgnoringGivenFields(associateDtoMapper.toDto(associate),
            "addressDto");

        assertEquals(addressDtoMapper.toDto(address), result.getAddressDto());
    }

    @Test
    void findSortedAssociates() {
        List<Associate> associateList = Collections.singletonList(associate);

        //logic for mock
        when(associateDao.getAssociates(any(Pageable.class), anyLong())).thenReturn(new PageImpl<>(associateList));

        //execute
        Page pageActual = associateService.findSortedAssociates(
            PageRequest.of(1, 1, Sort.by(Sort.Order.asc("id"))), userDetails);

        //verify
        verify(associateDao, atLeastOnce()).getAssociates(any(Pageable.class), anyLong());
        assertEquals(associateDtoMapper.toDtoList(associateList), pageActual.getContent());
    }

    @Test
    void delete() {
        when(associateDao.delete(anyLong(), anyLong())).thenReturn(true);
        when(associateDao.findById(anyLong(), anyLong())).thenReturn(associate);

        boolean result = associateService.delete(userDetails, associateDto.getId());

        assertTrue(result);
        verify(eventService, times(1)).create(any(Event.class));
    }

    @Test
    void getAssociatesByType() {
    }
}
