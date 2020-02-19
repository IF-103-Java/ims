package com.ita.if103java.ims.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.controller.AssociateController;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.exception.dao.AssociateEntityNotFoundException;
import com.ita.if103java.ims.exception.service.AssociateLimitReachedException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AssociateService;
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
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AssociateControllerTest {

    private MockMvc mockMvc;

    @Mock
    AssociateService associateService;

    @InjectMocks
    AssociateController associateController;

    private Pageable pageable;
    private AddressDto addressDto;
    private AssociateDto associateDto;
    private Long fakeId = 5L;
    private AssociateEntityNotFoundException associateEntityNotFoundException;
    private AssociateLimitReachedException associateLimitReachedException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(associateController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();

        this.pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id");

        this.addressDto = new AddressDto(1L, "country", "city", "address", "zip",
            10F, -30F);
        this.associateDto = new AssociateDto(1L, 1L, "Associate name", "test@test.com",
            "+380977959707", "additionalInfo", AssociateType.SUPPLIER, addressDto);

        this.associateEntityNotFoundException =
            new AssociateEntityNotFoundException("Failed to obtain associate during `select`, id = " + fakeId);

        this.associateLimitReachedException = new AssociateLimitReachedException("The maximum of " +
            associateDto.getType() + "s for account has been reached.");
    }

    @Test
    void testView_failFlow() throws Exception {
        when(associateService.view(any(UserDetailsImpl.class), eq(fakeId))).thenThrow(associateEntityNotFoundException);

        mockMvc.perform(get("/associates/" + fakeId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(associateEntityNotFoundException.getLocalizedMessage()));

        verify(associateService, times(1)).view(any(UserDetailsImpl.class), eq(fakeId));
    }

    @Test
    void testView_successFlow() throws Exception {
        when(associateService.view(any(UserDetailsImpl.class), eq(associateDto.getId()))).thenReturn(associateDto);

        mockMvc.perform(get("/associates/" + associateDto.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(associateDto.getId()))
            .andExpect(jsonPath("$.accountId").value(associateDto.getAccountId()))
            .andExpect(jsonPath("$.name").value(associateDto.getName()))
            .andExpect(jsonPath("$.email").value(associateDto.getEmail()))
            .andExpect(jsonPath("$.phone").value(associateDto.getPhone()))
            .andExpect(jsonPath("$.additionalInfo").value(associateDto.getAdditionalInfo()))
            .andExpect(jsonPath("$.type").value(associateDto.getType().toString()))
            .andExpect(jsonPath("$.addressDto.id").value(associateDto.getAddressDto().getId()))
            .andExpect(jsonPath("$.addressDto.country").value(associateDto.getAddressDto().getCountry()))
            .andExpect(jsonPath("$.addressDto.city").value(associateDto.getAddressDto().getCity()))
            .andExpect(jsonPath("$.addressDto.address").value(associateDto.getAddressDto().getAddress()))
            .andExpect(jsonPath("$.addressDto.zip").value(associateDto.getAddressDto().getZip()))
            .andExpect(jsonPath("$.addressDto.latitude").value(associateDto.getAddressDto().getLatitude()))
            .andExpect(jsonPath("$.addressDto.longitude").value(associateDto.getAddressDto().getLongitude()));

        verify(associateService, times(1)).view(any(UserDetailsImpl.class), eq(associateDto.getId()));
    }

    @Test
    void testFindAllSortedAssociates_successFlow() throws Exception {
        Page page = new PageImpl<>(Collections.singletonList(associateDto));

        when(associateService.findSortedAssociates(any(PageRequest.class), any(UserDetailsImpl.class)))
            .thenReturn(page);

        mockMvc.perform(get("/associates/?page=" + pageable.getPageNumber() +
            "&size=" + pageable.getPageSize() +
            "&sort=" + pageable.getSort().toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.numberOfElements").value(page.getTotalElements()));

        verify(associateService, times(1))
            .findSortedAssociates(any(PageRequest.class), any(UserDetailsImpl.class));
    }

    @Test
    void testCreate_failFlow() throws Exception {
        when(associateService.create(any(UserDetailsImpl.class), any(AssociateDto.class)))
            .thenThrow(associateLimitReachedException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(associateDto);

        mockMvc.perform(post("/associates/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(associateLimitReachedException.getLocalizedMessage()));

        verify(associateService, times(1))
            .create(any(UserDetailsImpl.class), any(AssociateDto.class));
    }

    @Test
    void testCreate_successFlow() throws Exception {
        when(associateService.create(any(UserDetailsImpl.class), any(AssociateDto.class))).thenReturn(associateDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(associateDto);

        mockMvc.perform(post("/associates/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(associateDto.getId()))
            .andExpect(jsonPath("$.accountId").value(associateDto.getAccountId()))
            .andExpect(jsonPath("$.name").value(associateDto.getName()))
            .andExpect(jsonPath("$.email").value(associateDto.getEmail()))
            .andExpect(jsonPath("$.phone").value(associateDto.getPhone()))
            .andExpect(jsonPath("$.additionalInfo").value(associateDto.getAdditionalInfo()))
            .andExpect(jsonPath("$.type").value(associateDto.getType().toString()))
            .andExpect(jsonPath("$.addressDto.id").value(associateDto.getAddressDto().getId()))
            .andExpect(jsonPath("$.addressDto.country").value(associateDto.getAddressDto().getCountry()))
            .andExpect(jsonPath("$.addressDto.city").value(associateDto.getAddressDto().getCity()))
            .andExpect(jsonPath("$.addressDto.address").value(associateDto.getAddressDto().getAddress()))
            .andExpect(jsonPath("$.addressDto.zip").value(associateDto.getAddressDto().getZip()))
            .andExpect(jsonPath("$.addressDto.latitude").value(associateDto.getAddressDto().getLatitude()))
            .andExpect(jsonPath("$.addressDto.longitude").value(associateDto.getAddressDto().getLongitude()));

        verify(associateService, times(1))
            .create(any(UserDetailsImpl.class), any(AssociateDto.class));
    }

    @Test
    void testUpdate_successFlow() throws Exception {
        when(associateService.update(any(UserDetailsImpl.class), any(AssociateDto.class))).thenReturn(associateDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(associateDto);

        mockMvc.perform(put("/associates/" + associateDto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(associateDto.getId()))
            .andExpect(jsonPath("$.accountId").value(associateDto.getAccountId()))
            .andExpect(jsonPath("$.name").value(associateDto.getName()))
            .andExpect(jsonPath("$.email").value(associateDto.getEmail()))
            .andExpect(jsonPath("$.phone").value(associateDto.getPhone()))
            .andExpect(jsonPath("$.additionalInfo").value(associateDto.getAdditionalInfo()))
            .andExpect(jsonPath("$.type").value(associateDto.getType().toString()))
            .andExpect(jsonPath("$.addressDto.id").value(associateDto.getAddressDto().getId()))
            .andExpect(jsonPath("$.addressDto.country").value(associateDto.getAddressDto().getCountry()))
            .andExpect(jsonPath("$.addressDto.city").value(associateDto.getAddressDto().getCity()))
            .andExpect(jsonPath("$.addressDto.address").value(associateDto.getAddressDto().getAddress()))
            .andExpect(jsonPath("$.addressDto.zip").value(associateDto.getAddressDto().getZip()))
            .andExpect(jsonPath("$.addressDto.latitude").value(associateDto.getAddressDto().getLatitude()))
            .andExpect(jsonPath("$.addressDto.longitude").value(associateDto.getAddressDto().getLongitude()));

        verify(associateService, times(1))
            .update(any(UserDetailsImpl.class), any(AssociateDto.class));
    }

    @Test
    void testDelete_failFlow() throws Exception {
        when(associateService.delete(any(UserDetailsImpl.class), eq(fakeId))).thenThrow(associateEntityNotFoundException);

        mockMvc.perform(delete("/associates/" + fakeId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(associateEntityNotFoundException.getLocalizedMessage()));
    }

    @Test
    void testDelete_successFlow() throws Exception {
        when(associateService.delete(any(UserDetailsImpl.class), eq(associateDto.getId()))).thenReturn(true);

        mockMvc.perform(delete("/associates/" + associateDto.getId())
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }
}
