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
import com.ita.if103java.ims.entity.EventType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.AssociateEntityNotFoundException;
import com.ita.if103java.ims.exception.service.AssociateLimitReachedException;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.AssociateDtoMapper;
import com.ita.if103java.ims.mapper.dto.SavedItemAssociateDtoMapper;
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

    @InjectMocks
    private AssociateServiceImpl associateService;

    private SavedItemAssociateDtoMapper savedItemAssociateDtoMapper = new SavedItemAssociateDtoMapper();
    private AssociateDtoMapper associateDtoMapper = new AssociateDtoMapper();
    private AddressDtoMapper addressDtoMapper = new AddressDtoMapper();


    private Associate associate = new Associate(1L, 1L, "Associate name", "test@test.com",
        "+380977959707", "additionalInfo", AssociateType.SUPPLIER, true);

    private Address address = new Address("country", "city", "address", "zip", 10F, -30F);

    private AssociateDto associateDto;

    private AddressDto addressDto = addressDtoMapper.toDto(address);

    private UserDetailsImpl userDetails;

    private Long fakeId = 5L;

    AssociateEntityNotFoundException associateEntityNotFoundException =
        new AssociateEntityNotFoundException("Failed to obtain associate during `select`, id = " + fakeId);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        associateService = new AssociateServiceImpl(associateDao, addressDao,
            associateDtoMapper, addressDtoMapper,
            eventService, locationService,
            savedItemAssociateDtoMapper);

        AccountType accountType = new AccountType();
        accountType.setMaxSuppliers(1);
        accountType.setMaxClients(1);

        User user = new User();
        user.setAccountId(1L);
        this.userDetails = new UserDetailsImpl(user, accountType);

        this.associateDto = new AssociateDto(1L, 1L, "Associate name", "test@test.com",
            "+380977959707", "additionalInfo", AssociateType.SUPPLIER, true, addressDto);

    }

    @Test
    void testCreate_successFlow() {
        when(associateDao.create(userDetails.getUser().getAccountId(), associate)).thenReturn(associate);
        when(locationService.getLocationByAddress(anyString())).thenReturn(new com.ita.if103java.ims.dto.warehouse.advice.Address.Geo(1F, 1F));

        AssociateDto result = associateService.create(userDetails, associateDto);

        verify(locationService, times(1)).getLocationByAddress(anyString());
        verify(addressDao, times(1)).createAssociateAddress(anyLong(), any(Address.class));
        verify(associateDao, times(1)).create(userDetails.getUser().getAccountId(),
            associate);
        verify(eventService, times(1)).create(any(Event.class));

        assertEquals(associateDtoMapper.toDto(associate), result);
    }

    @Test
    void testCreate_failFlow() {
        List<Associate> associateList = Collections.singletonList(associate);

        when(associateDao.findByAccountId(userDetails.getUser().getAccountId())).thenReturn(associateList);

        assertThrows(AssociateLimitReachedException.class,
            () -> {associateService.create(userDetails, associateDto);});
    }

    @Test
    void testCreate_failFlowTypeClient() {
        associateDto.setType(AssociateType.CLIENT);
        associate = associateDtoMapper.toEntity(associateDto);

        List<Associate> associateList = Collections.singletonList(associate);

        when(associateDao.findByAccountId(userDetails.getUser().getAccountId())).thenReturn(associateList);

        assertThrows(AssociateLimitReachedException.class,
            () -> {associateService.create(userDetails, associateDto);});
    }


    @Test
    void testView_successFlow() {
        when(associateDao.findById(userDetails.getUser().getAccountId(), associateDto.getId())).thenReturn(associate);
        when(addressDao.findByAssociateId(associateDto.getId())).thenReturn(address);

        AssociateDto result = associateService.view(userDetails, associateDto.getId());

        assertThat(result).isEqualToIgnoringGivenFields(associateDtoMapper.toDto(associate),
            "addressDto");

        assertEquals(addressDtoMapper.toDto(address), result.getAddressDto());
    }

    @Test
    void testView_failFlow() {
        when(associateDao.findById(userDetails.getUser().getAccountId(), fakeId)).thenThrow(associateEntityNotFoundException);

        assertThrows(AssociateEntityNotFoundException.class,
            () -> {associateService.view(userDetails, fakeId);});
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
