package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.exception.service.ItemValidateInputException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SavedItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    ItemService itemService;

    @InjectMocks
    SavedItemController savedItemController;

    private SavedItemDto savedItemDto, savedItemDto2;
    private ItemTransactionRequestDto itemTransactionRequestDto;
    private SavedItemNotFoundException savedItemNotFoundException;
    private ItemNotEnoughCapacityInWarehouseException itemNotEnoughCapacityInWarehouseException;
    private ItemNotEnoughQuantityException itemNotEnoughQuantityException;
    private ItemValidateInputException itemValidateInputException;

    private Long notValidSavedItemId = 5L;
    private Long notValidWarehouseId = 25L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders
            .standaloneSetup(savedItemController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();

        this.savedItemDto = new SavedItemDto();
        this.savedItemDto.setId(1L);
        this.savedItemDto.setWarehouseId(37L);
        this.savedItemDto.setQuantity(5);
        this.savedItemDto.setItemId(108L);

        this.savedItemDto2 = new SavedItemDto();
        this.savedItemDto2.setId(2L);
        this.savedItemDto2.setWarehouseId(38L);
        this.savedItemDto2.setQuantity(7);
        this.savedItemDto2.setItemId(107L);

        this.itemTransactionRequestDto = new ItemTransactionRequestDto();
        this.itemTransactionRequestDto.setItemId(7L);
        this.itemTransactionRequestDto.setSavedItemId(30L);
        this.itemTransactionRequestDto.setSourceWarehouseId(38L);
        this.itemTransactionRequestDto.setDestinationWarehouseId(37L);
        this.itemTransactionRequestDto.setQuantity(5L);
        this.itemTransactionRequestDto.setAssociateId(40L);

        this.savedItemNotFoundException = new SavedItemNotFoundException("Failed to get savedItem during `select` {id" +
            " = " + notValidSavedItemId + "}");
        this.itemNotEnoughCapacityInWarehouseException = new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it " +
            "doesn't " +
            " have enough capacity {warehouse_id = " + notValidWarehouseId + "}");
        this.itemNotEnoughQuantityException = new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse " +
            "needed" +
            " quantity of items {warehouse_id = " + notValidWarehouseId +  "}");
        this.itemValidateInputException = new ItemValidateInputException("Failed to create item, because exist the same");

    }

    @Test
    void addSavedItem_successFlow() throws Exception{
        when(itemService.addSavedItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenReturn(savedItemDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(post("/savedItems")
        .contentType(MediaType.APPLICATION_JSON)
        .content(resultJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedItemDto.getId()))
        .andExpect(jsonPath("$.itemId").value(savedItemDto.getItemId()))
        .andExpect(jsonPath("$.quantity").value(savedItemDto.getQuantity()))
        .andExpect(jsonPath("$.warehouseId").value(savedItemDto.getWarehouseId()));

        verify(itemService, times(1)).addSavedItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void addSavedItem_failFlowNotValidInputs() throws Exception{
        when(itemService.addSavedItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenThrow(itemValidateInputException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(post("/savedItems")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(itemValidateInputException.getLocalizedMessage()));

        verify(itemService, times(1)).addSavedItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void addSavedItem_failFlowNotEnoughCapacityInWarehouse() throws Exception {
        when(itemService.addSavedItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenThrow(itemNotEnoughCapacityInWarehouseException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(post("/savedItems")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(itemNotEnoughCapacityInWarehouseException.getLocalizedMessage()));

        verify(itemService, times(1)).addSavedItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void moveSavedItem_successFlow() throws Exception{
        when(itemService.moveItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenReturn(true);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(put("/savedItems/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk());

        verify(itemService, times(1)).moveItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void moveSavedItem_failFlowNotValidInputs() throws Exception{
        when(itemService.moveItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenThrow(itemValidateInputException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(put("/savedItems/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(itemValidateInputException.getLocalizedMessage()));

        verify(itemService, times(1)).moveItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void moveSavedItem_failFlowNotEnoughCapacityInWarehouse() throws Exception{
        when(itemService.moveItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenThrow(itemNotEnoughCapacityInWarehouseException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(put("/savedItems/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(itemNotEnoughCapacityInWarehouseException.getLocalizedMessage()));

        verify(itemService, times(1)).moveItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void outcomeItem_successFlow() throws Exception{
        when(itemService.outcomeItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenReturn(savedItemDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(put("/savedItems/outcome")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedItemDto.getId()))
            .andExpect(jsonPath("$.itemId").value(savedItemDto.getItemId()))
            .andExpect(jsonPath("$.quantity").value(savedItemDto.getQuantity()))
            .andExpect(jsonPath("$.warehouseId").value(savedItemDto.getWarehouseId()));

        verify(itemService, times(1)).outcomeItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void outcomeItem_failFlowNotValidInputs() throws Exception{
        when(itemService.outcomeItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenThrow(itemValidateInputException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(put("/savedItems/outcome")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(itemValidateInputException.getLocalizedMessage()));

        verify(itemService, times(1)).outcomeItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void outcomeItem_failFlowNotEnoughQuantity() throws Exception{
        when(itemService.outcomeItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class))).thenThrow(itemNotEnoughQuantityException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemTransactionRequestDto);

        mockMvc.perform(put("/savedItems/outcome")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(itemNotEnoughQuantityException.getLocalizedMessage()));

        verify(itemService, times(1)).outcomeItem(any(ItemTransactionRequestDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void findByItemId_successFlow() throws Exception{
        List<SavedItemDto> savedItemDtoList = List.of(savedItemDto, savedItemDto2);

        when(itemService.findByItemId(anyLong(), any(UserDetailsImpl.class))).thenReturn(savedItemDtoList);

        mockMvc.perform(get("/savedItems/itemId/" + savedItemDto.getItemId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(savedItemDtoList.get(0).getId()))
        .andExpect(jsonPath("$.[1].quantity").value(savedItemDtoList.get(1).getQuantity()));

        verify(itemService, times(1)).findByItemId(eq(savedItemDto.getItemId()), any(UserDetailsImpl.class));
    }

    @Test
    void findByItemId_failFlowSavedItemNotFoundException() throws Exception{
        when(itemService.findByItemId(anyLong(), any(UserDetailsImpl.class))).thenThrow(savedItemNotFoundException);

        mockMvc.perform(get("/savedItems/itemId/" + savedItemDto.getItemId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(savedItemNotFoundException.getLocalizedMessage()));

            verify(itemService, times(1)).findByItemId(eq(savedItemDto.getItemId()), any(UserDetailsImpl.class));
    }

    @Test
    void findSavedItemById_successFlow() throws Exception {
        when(itemService.findSavedItemById(anyLong(), any(UserDetailsImpl.class))).thenReturn(savedItemDto);

        mockMvc.perform(get("/savedItems/" + savedItemDto.getItemId())
        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedItemDto.getId()))
            .andExpect(jsonPath("$.itemId").value(savedItemDto.getItemId()))
            .andExpect(jsonPath("$.quantity").value(savedItemDto.getQuantity()))
            .andExpect(jsonPath("$.warehouseId").value(savedItemDto.getWarehouseId()));

        verify(itemService, times(1)).findSavedItemById(eq(savedItemDto.getItemId()), any(UserDetailsImpl.class));
    }

    @Test
    void findSavedItemById_failFlow() throws Exception {
        when(itemService.findSavedItemById(anyLong(), any(UserDetailsImpl.class))).thenThrow(savedItemNotFoundException);

        mockMvc.perform(get("/savedItems/" + savedItemDto.getItemId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(savedItemNotFoundException.getLocalizedMessage()));

        verify(itemService, times(1)).findSavedItemById(eq(savedItemDto.getItemId()), any(UserDetailsImpl.class));
    }

}
