package com.ita.if103java.ims.service;

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
import com.ita.if103java.ims.exception.dao.AssociateEntityNotFoundException;
import com.ita.if103java.ims.exception.service.AssociateLimitReachedException;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.AssociateDtoMapper;
import com.ita.if103java.ims.mapper.dto.SavedItemAssociateDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.AssociateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private Address address;
    private Associate associate;
    private AddressDto addressDto;
    private AssociateDto associateDto;
    private List<Associate> associateList;
    private UserDetailsImpl userDetails;

    private Long fakeId = 5L;
    private PageRequest pageRequest = PageRequest.of(0, 15, Sort.Direction.ASC, "id");

    private AssociateEntityNotFoundException associateEntityNotFoundException;


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

        this.address = new Address("country", "city", "address", "zip", 10F, -30F);
        this.addressDto = addressDtoMapper.toDto(address);

        this.associateDto = new AssociateDto(1L, 1L, "Associate name", "test@test.com",
            "+380977959707", "additionalInfo", AssociateType.SUPPLIER, true, addressDto);
        this.associate = associateDtoMapper.toEntity(associateDto);

        this.associateList = Collections.singletonList(associate);

        this.associateEntityNotFoundException =
            new AssociateEntityNotFoundException("Failed to obtain associate during `select`, id = " + fakeId);
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
        when(associateDao.findByAccountId(userDetails.getUser().getAccountId())).thenReturn(associateList);

        assertThrows(AssociateLimitReachedException.class,
            () -> {
                associateService.create(userDetails, associateDto);
            });
    }

    @Test
    void testCreate_failFlowTypeClient() {
        associateDto.setType(AssociateType.CLIENT);
        associate = associateDtoMapper.toEntity(associateDto);
        associateList = Collections.singletonList(associate);

        when(associateDao.findByAccountId(userDetails.getUser().getAccountId())).thenReturn(associateList);

        assertThrows(AssociateLimitReachedException.class,
            () -> {
                associateService.create(userDetails, associateDto);
            });
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
            () -> {
                associateService.view(userDetails, fakeId);
            });
    }

    @Test
    void testFindSortedAssociates_successFlow() {
        when(associateDao.getAssociates(any(Pageable.class), anyLong())).thenReturn(new PageImpl<>(associateList));

        Page pageActual = associateService.findSortedAssociates(pageRequest, userDetails);

        verify(associateDao, times(1)).getAssociates(any(Pageable.class), anyLong());
        assertEquals(associateDtoMapper.toDtoList(associateList), pageActual.getContent());
    }

    @Test
    void testDelete_successFlow() {
        when(associateDao.delete(anyLong(), anyLong())).thenReturn(true);
        when(associateDao.findById(anyLong(), anyLong())).thenReturn(associate);

        boolean result = associateService.delete(userDetails, associateDto.getId());

        verify(associateDao, times(1)).delete(anyLong(), anyLong());
        verify(eventService, times(1)).create(any(Event.class));
        assertTrue(result);
    }

    @Test
    void testDelete_failFlow() {
        when(associateDao.findById(anyLong(), eq(fakeId))).thenThrow(associateEntityNotFoundException);

        assertThrows(AssociateEntityNotFoundException.class,
            () -> {
                associateService.delete(userDetails, fakeId);
            });

        verify(associateDao, times(1)).findById(anyLong(), eq(fakeId));
        verify(eventService, never()).create(any(Event.class));
    }

    @Test
    void testGetAssociatesByType_successFlow() {
        when(associateDao.getAssociatesByType(anyLong(), ArgumentMatchers.<AssociateType>any()))
            .thenReturn(associateList);

        assertEquals(savedItemAssociateDtoMapper.toDtoList(associateList),
            associateService.getAssociatesByType(userDetails, AssociateType.SUPPLIER));
    }
}
