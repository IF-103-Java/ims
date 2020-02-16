package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.AssociateLimitReachedException;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.AssociateDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.AssociateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class AssociateServiceTest {

    @InjectMocks
    private AssociateServiceImpl associateService;

    @Mock
    private AssociateDao associateDao;

    @Mock
    private AddressDao addressDao;

    @Mock
    private AssociateDtoMapper associateDtoMapper;

    @Mock
    private AddressDtoMapper addressDtoMapper;

    @Mock
    private EventService eventService;

    @Mock
    private LocationService locationService;

    @Test
    void create() {
        // init data
        AccountType accountType = new AccountType();
        accountType.setMaxSuppliers(20);

        User user = new User();
        user.setAccountId(1L);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, accountType);

        AssociateDto associateDto = new AssociateDto();
        associateDto.setName("Associate name");
        associateDto.setType(AssociateType.SUPPLIER);

        Associate associate = new Associate();
        associate.setName("Associate name");
        associate.setType(AssociateType.SUPPLIER);

        // logic for mock
        when(associateDao.findByAccountId(anyLong())).thenReturn(new ArrayList<>());
        when(associateDtoMapper.toEntity(associateDto)).thenReturn(associate);

        // execute method
        associateService.create(userDetails, associateDto);

        //verify
        verify(associateDao, atLeastOnce()).create(anyLong(), any(Associate.class));
    }

    @Test
    void createThrowsException() {
        // init data
        AccountType accountType = new AccountType();
        accountType.setMaxSuppliers(1);

        User user = new User();
        user.setAccountId(1L);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, accountType);

        AssociateDto associateDto = new AssociateDto();
        associateDto.setName("Associate name");
        associateDto.setType(AssociateType.SUPPLIER);

        Associate associate = new Associate();
        associate.setName("Associate name");
        associate.setType(AssociateType.SUPPLIER);

        // logic for mock
        when(associateDao.findByAccountId(anyLong())).thenReturn(Stream
            .of(associate).collect(Collectors.toList()));

        // execute method
        assertThrows(AssociateLimitReachedException.class,
            () -> {associateService.create(userDetails, associateDto);});
    }


    @Test
    void view() {
        // init data
        AccountType accountType = new AccountType();
        accountType.setMaxSuppliers(1);

        User user = new User();
        user.setAccountId(1L);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, accountType);

        AssociateDto associateDto = new AssociateDto();
        associateDto.setName("Associate name");
        associateDto.setType(AssociateType.SUPPLIER);

        Associate associate = new Associate();
        associate.setName("Associate name");
        associate.setType(AssociateType.SUPPLIER);

        // logic for mock
        when(associateDao.findById(anyLong(), anyLong())).thenReturn(associate);
        when(associateDtoMapper.toDto(any(Associate.class))).thenReturn(associateDto);
        when(addressDao.findByAssociateId(anyLong())).thenReturn(new Address());
        when(addressDtoMapper.toDto(any(Address.class))).thenReturn(new AddressDto());

        //execute
        AssociateDto associateActual = associateService.view(userDetails, 1L);

        // verify
        verify(associateDao, atLeastOnce()).findById(anyLong(), anyLong());
        verify(addressDao, atLeastOnce()).findByAssociateId(anyLong());
        assertEquals(associateDto, associateActual);
        assertNotNull(associateActual.getAddressDto());
    }


    @Test
    void findSortedAssociates() {
        // init data
        User user = new User();
        user.setAccountId(1L);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Associate associate1 = new Associate();
        Associate associate2 = new Associate();

        List<Associate> associates = Stream.of(
            associate1, associate2).collect(Collectors.toList());

        //logic for mock
        when(associateDao.getAssociates(any(Pageable.class), anyLong())).thenReturn(new PageImpl<>(associates));

        //execute
        Page pageActual = associateService.findSortedAssociates(
            PageRequest.of(1, 1, Sort.Direction.ASC), userDetails);

        //verify
        verify(associateDao, atLeastOnce()).getAssociates(any(Pageable.class), anyLong());
        assertEquals(associates, pageActual.getContent());
    }
}
