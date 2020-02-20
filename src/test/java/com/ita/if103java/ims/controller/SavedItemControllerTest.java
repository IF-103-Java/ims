package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.exception.service.ItemValidateInputException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class SavedItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    ItemService itemService;

    @InjectMocks
    SavedItemController savedItemController;
    private SavedItemDto savedItemDto, savedItemDto2;
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
        this.savedItemNotFoundException = new SavedItemNotFoundException("Failed to get savedItem during `select` {id" +
            " = " + notValidSavedItemId + "}");
        this.itemNotEnoughCapacityInWarehouseException = new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it " +
            "doesn't " +
            " have enough capacity {warehouse_id = " + notValidWarehouseId + "}");
//        this.itemNotEnoughQuantityException = new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse " +
//            "needed" +
//            " quantity of items {warehouse_id = " + notValidWarehouseId + ", quantity = " +
//            itemTransaction.getQuantity() + "}");
        this.itemValidateInputException = new ItemValidateInputException();

    }
}
