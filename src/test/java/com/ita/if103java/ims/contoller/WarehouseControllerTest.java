package com.ita.if103java.ims.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.controller.WarehouseController;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.WarehouseNotFoundException;
import com.ita.if103java.ims.exception.service.MaxWarehousesLimitReachedException;
import com.ita.if103java.ims.exception.service.WarehouseCreateException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WarehouseControllerTest {
    private MockMvc mockMvc;

    @Mock
    WarehouseService warehouseService;

    @InjectMocks
    WarehouseController warehouseController;
    private Pageable pageable;
    private WarehouseDto warehouseDto;
    private AddressDto addressDto;
    private Long id = 1L;
    private WarehouseCreateException warehouseCreateException;
    private WarehouseNotFoundException warehouseNotFoundException;
    private MaxWarehousesLimitReachedException maxWarehousesLimitReachedException;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
        mockMvc = MockMvcBuilders
            .standaloneSetup(warehouseController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

        this.addressDto = new AddressDto(1L, "country", "city", "address", "zip",
            43.142F, -95.049F);
        this.warehouseDto = new WarehouseDto(1L, "Warehouse", "goods", null, false,
            null, 2L, null, true, addressDto);

        warehouseCreateException = new WarehouseCreateException();
        warehouseNotFoundException = new WarehouseNotFoundException();
        maxWarehousesLimitReachedException = new MaxWarehousesLimitReachedException("The maximum number of warehouses has been reached");
    }

    @Test
    void findAll_successFlow() throws Exception {
        Page page = new PageImpl<>(Collections.singletonList(warehouseDto));

        when(warehouseService.findAllTopLevel(any(PageRequest.class), any(UserDetailsImpl.class)))
            .thenReturn(page);

        mockMvc.perform(get("/warehouses/?page=" + pageable.getPageNumber() +
            "&size=" + pageable.getPageSize() +
            "&sort=" + pageable.getSort().toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.numberOfElements").value(page.getTotalElements()));

        verify(warehouseService, times(1))
            .findAllTopLevel(any(PageRequest.class), any(UserDetailsImpl.class));
    }

    @Test
    void add_failFlow() throws Exception {
        when(warehouseService.add(any(WarehouseDto.class), any(UserDetailsImpl.class)))
            .thenThrow(maxWarehousesLimitReachedException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(warehouseDto);

        mockMvc.perform(post("/warehouses/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(maxWarehousesLimitReachedException.getLocalizedMessage())
            );


        verify(warehouseService, times(1))
            .add(any(WarehouseDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void add_successFlow() throws Exception {
        when(warehouseService.add(any(WarehouseDto.class), any(UserDetailsImpl.class))).thenReturn(warehouseDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(warehouseDto);

        mockMvc.perform(post("/warehouses/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(warehouseDto.getId()))
            .andExpect(jsonPath("$.name").value(warehouseDto.getName()))
            .andExpect(jsonPath("$.info").value(warehouseDto.getInfo()))
            .andExpect(jsonPath("$.capacity").value(warehouseDto.getCapacity()))
            .andExpect(jsonPath("$.parentID").value(warehouseDto.getParentID()))
            .andExpect(jsonPath("$.accountID").value(warehouseDto.getAccountID()))
            .andExpect(jsonPath("$.topWarehouseID").value(warehouseDto.getTopWarehouseID()))
            .andExpect(jsonPath("$.active").value(warehouseDto.isActive()))
            .andExpect(jsonPath("$.addressDto.id").value(warehouseDto.getAddressDto().getId()))
            .andExpect(jsonPath("$.addressDto.country").value(warehouseDto.getAddressDto().getCountry()))
            .andExpect(jsonPath("$.addressDto.city").value(warehouseDto.getAddressDto().getCity()))
            .andExpect(jsonPath("$.addressDto.address").value(warehouseDto.getAddressDto().getAddress()))
            .andExpect(jsonPath("$.addressDto.zip").value(warehouseDto.getAddressDto().getZip()))
            .andExpect(jsonPath("$.addressDto.latitude").value(warehouseDto.getAddressDto().getLatitude()))
            .andExpect(jsonPath("$.addressDto.longitude").value(warehouseDto.getAddressDto().getLongitude()));

        verify(warehouseService, times(1))
            .add(any(WarehouseDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void update_successFlow() throws Exception {
        when(warehouseService.update(any(WarehouseDto.class), any(UserDetailsImpl.class))).thenReturn(warehouseDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(warehouseDto);

        mockMvc.perform(post("/warehouses/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(warehouseDto.getId()))
            .andExpect(jsonPath("$.name").value(warehouseDto.getName()))
            .andExpect(jsonPath("$.info").value(warehouseDto.getInfo()))
            .andExpect(jsonPath("$.capacity").value(warehouseDto.getCapacity()))
            .andExpect(jsonPath("$.parentID").value(warehouseDto.getParentID()))
            .andExpect(jsonPath("$.accountID").value(warehouseDto.getAccountID()))
            .andExpect(jsonPath("$.topWarehouseID").value(warehouseDto.getTopWarehouseID()))
            .andExpect(jsonPath("$.active").value(warehouseDto.isActive()))
            .andExpect(jsonPath("$.addressDto.id").value(warehouseDto.getAddressDto().getId()))
            .andExpect(jsonPath("$.addressDto.country").value(warehouseDto.getAddressDto().getCountry()))
            .andExpect(jsonPath("$.addressDto.city").value(warehouseDto.getAddressDto().getCity()))
            .andExpect(jsonPath("$.addressDto.address").value(warehouseDto.getAddressDto().getAddress()))
            .andExpect(jsonPath("$.addressDto.zip").value(warehouseDto.getAddressDto().getZip()))
            .andExpect(jsonPath("$.addressDto.latitude").value(warehouseDto.getAddressDto().getLatitude()))
            .andExpect(jsonPath("$.addressDto.longitude").value(warehouseDto.getAddressDto().getLongitude()));

        verify(warehouseService, times(1))
            .update(any(WarehouseDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void delete_failFlow() throws Exception {
        when(warehouseService.softDelete(eq(id), any(UserDetailsImpl.class))).thenThrow(warehouseNotFoundException);

        mockMvc.perform(delete("/warehouses/" + id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(warehouseNotFoundException.getLocalizedMessage()));
    }

    @Test
    void delete_successFlow() throws Exception {
        when(warehouseService.softDelete(eq(warehouseDto.getId()), any(UserDetailsImpl.class))).thenReturn(true);

        mockMvc.perform(delete("/warehouses/" + warehouseDto.getId())
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

}
