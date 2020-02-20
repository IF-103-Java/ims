package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.DateType;
import com.ita.if103java.ims.entity.PopType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.DashboardDataNotFoundException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.SecurityInterceptor;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ita.if103java.ims.security.SecurityInterceptor.init;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    DashboardService dashboardService;

    @InjectMocks
    DashboardController dashboardController;

    private User user;
    private UserDetailsImpl userDetails;
    private AccountType accountType;
    private ZonedDateTime currentDateTime;
    private DashboardDataNotFoundException dashboardDataNotFoundException;
    private Long fakeAccountId = 1L;
    private Long fakeWarehouseId = 4L;
    private int fakeMinQuantity = 5;
    private EndingItemsDto endingItemsDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(dashboardController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .addInterceptors(new SecurityInterceptor())
            .setCustomArgumentResolvers(
                new AuthenticationPrincipalArgumentResolver(),
                new PageableHandlerMethodArgumentResolver())
            .build();

        currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());

        user = new User(
            1L,
            "First name",
            "Last name",
            "pull@gmail.com",
            "qwerty123",
            Role.ROLE_ADMIN,
            currentDateTime,
            currentDateTime,
            true,
            "klltnpt",
            1L);

        accountType = new AccountType(
            2L,
            "Premium",
            300.0,
            2,
            100,
            100,
            100,
            100,
            100,
            true,
            true,
            true);

        userDetails = new UserDetailsImpl(user, accountType);
        endingItemsDto = new EndingItemsDto(1L, "warehouse_a", "apple", 10);
        dashboardDataNotFoundException = new DashboardDataNotFoundException("Dashboard not found");
    }

    @Test
    void testGetWarehouseLoad_failFlow() throws Exception {

        when(dashboardService.getWarehouseLoad(anyLong())).thenThrow(dashboardDataNotFoundException);

        mockMvc.perform(
            get("/dashboard/warehouseLoad")
                .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(dashboardDataNotFoundException.getLocalizedMessage()));

    }

    @Test
    void testGetWarehouseLoad_successFlow() throws Exception{
        List<WarehouseLoadDto> list = new ArrayList();
        list.add(new WarehouseLoadDto(fakeWarehouseId, "warehouse_a", 100L, 10L));

        when(dashboardService.getWarehouseLoad(anyLong())).thenReturn(list);

        mockMvc.perform(
            get("/dashboard/warehouseLoad")
                .principal(init(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..id").value(fakeWarehouseId.intValue()))
            .andExpect(jsonPath("$..name").value("warehouse_a"))
            .andExpect(jsonPath("$..capacity").value(100))
            .andExpect(jsonPath("$..charge").value(10));

        verify(dashboardService, times(1))
            .getWarehouseLoad(userDetails.getUser().getAccountId());
    }

    @Test
    void testGetEndingItems_failFlow() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "quantity");

        when(dashboardService.getEndingItems(any(PageRequest.class), eq(fakeMinQuantity), eq(fakeAccountId)))
            .thenThrow(dashboardDataNotFoundException);

        mockMvc.perform(
            get("/dashboard/endingItems?page=" + pageable.getPageNumber() +
                "&size=" + pageable.getPageSize() +
                "&sort=" + pageable.getSort().toString() +
                "&minQuantity=" + fakeMinQuantity)
                .principal(init(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(dashboardDataNotFoundException.getLocalizedMessage()));
    }

    @Test
    void testGetEndingItems_successFlow() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "quantity");

        Page<EndingItemsDto> page = new PageImpl<>(Collections.singletonList(endingItemsDto));

        when(dashboardService.getEndingItems(any(PageRequest.class), eq(fakeMinQuantity), eq(fakeAccountId)))
            .thenReturn(page);

        mockMvc.perform(
            get("/dashboard/endingItems?page=" + pageable.getPageNumber() +
                "&size=" + pageable.getPageSize() +
                "&sort=" + pageable.getSort().toString() +
                "&minQuantity=" + fakeMinQuantity)
                .principal(init(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.numberOfElements").value(page.getTotalElements()));

        verify(dashboardService, times(1))
            .getEndingItems(any(PageRequest.class), eq(fakeMinQuantity), eq(fakeAccountId));
    }

    @Test
    void testPopularItems_failFlow() throws Exception {
        PopularItemsRequestDto popularItems =
            new PopularItemsRequestDto(3, DateType.YEAR, PopType.TOP, "17-02-2020");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(popularItems);

        when(dashboardService.getPopularItems(any(PopularItemsRequestDto.class), anyLong()))
            .thenThrow(dashboardDataNotFoundException);

        mockMvc.perform(
            post("/dashboard/popularityItems/")
                .principal(init(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(resultJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(dashboardDataNotFoundException.getLocalizedMessage()));
    }

    @Test
    void testPopularItems_successFlow() throws Exception {
        PopularItemsRequestDto popularItems =
            new PopularItemsRequestDto(3, DateType.YEAR, PopType.TOP, "17-02-2020");

        List<PopularItemsDto> list = new ArrayList<>();
        list.add(new PopularItemsDto("apple", 10L));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(popularItems);

        when(dashboardService.getPopularItems(any(PopularItemsRequestDto.class), anyLong())).thenReturn(list);

        mockMvc.perform(
            post("/dashboard/popularityItems/")
                .principal(init(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..name").value(list.get(0).getName()))
            .andExpect(jsonPath("$..quantity").value(list.get(0).getQuantity().intValue()));

        verify(dashboardService, times(1))
            .getPopularItems(any(PopularItemsRequestDto.class), anyLong());
    }

    @Test
    public void testPremiumLoad_failFlow() throws Exception {

        when(dashboardService.getPreLoad(anyLong(), anyLong())).thenThrow(dashboardDataNotFoundException);

        mockMvc.perform(
            get("/dashboard/premiumLoad/" + fakeWarehouseId)
                .principal(init(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(dashboardDataNotFoundException.getLocalizedMessage()));


        verify(dashboardService, times(1))
            .getPreLoad(anyLong(), anyLong());
    }

    @Test
    public void testPremiumLoad_successFlow() throws Exception {
        WarehousePremiumStructDto wpsd = new WarehousePremiumStructDto(
            fakeWarehouseId,
            "warehouse_A",
            10L,
            100L,
            List
                .of(new WarehousePremiumStructDto(
                        fakeWarehouseId + 1,
                        "warehouse_B",
                        3L,
                        60L,
                        null),
                    new WarehousePremiumStructDto(
                        fakeWarehouseId + 2,
                        "warehouse_C",
                        7L,
                        40L,
                        null)
                )
            );
        when(dashboardService.getPreLoad(anyLong(), anyLong())).thenReturn(wpsd);

        mockMvc.perform(
            get("/dashboard/premiumLoad/" + fakeWarehouseId)
                .principal(init(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(wpsd.getId()))
            .andExpect(jsonPath("$.name").value(wpsd.getName()))
            .andExpect(jsonPath("$.charge").value(wpsd.getCharge()))
            .andExpect(jsonPath("$.capacity").value(wpsd.getCapacity()))
            .andExpect(jsonPath("$.childs", hasSize(2)));

        verify(dashboardService, times(1))
            .getPreLoad(anyLong(), anyLong());
    }
}

