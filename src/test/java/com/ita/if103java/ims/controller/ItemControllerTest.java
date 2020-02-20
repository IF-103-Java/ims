package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.exception.dao.ItemNotFoundException;
import com.ita.if103java.ims.exception.service.ItemDuplicateException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.ItemService;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

public class ItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    private ItemDto itemDto, itemDto2;
    private Pageable pageable;
    private ItemDuplicateException itemDuplicateException;
    private ItemNotFoundException itemNotFoundException;
    private Long notValidId = 5L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders
            .standaloneSetup(itemController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();

        this.pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id");

        this.itemDto = new ItemDto();
        this.itemDto.setId(3L);
        this.itemDto.setName("Apple");
        this.itemDto.setUnit("box");
        this.itemDto.setActive(true);
        this.itemDto.setDescription("tasty apple");
        this.itemDto.setVolume(5);

        this.itemDto2 = new ItemDto();
        this.itemDto.setId(4L);
        this.itemDto2.setName("Apple2");
        this.itemDto2.setUnit("box");
        this.itemDto2.setActive(true);
        this.itemDto2.setDescription("tasty apple2");
        this.itemDto2.setVolume(5);

        this.itemDuplicateException = new ItemDuplicateException("Failed to create item, because exist the same ");
        this.itemNotFoundException = new ItemNotFoundException("Failed to get item during `select` {id = " + notValidId + "}");
      }
    @Test
    void addItem_successFlow() throws Exception {
        when(itemService.addItem(any(ItemDto.class), any(UserDetailsImpl.class))).thenReturn(itemDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(itemDto.getId()))
            .andExpect(jsonPath("$.accountId").value(itemDto.getAccountId()))
            .andExpect(jsonPath("$.name").value(itemDto.getName()))
            .andExpect(jsonPath("$.unit").value(itemDto.getUnit()))
            .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
            .andExpect(jsonPath("$.volume").value(itemDto.getVolume()))
            .andExpect(jsonPath("$.active").value(itemDto.isActive()));

        verify(itemService, times(1)).addItem(any(ItemDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void addItem_duplicateFlow() throws Exception {
        when(itemService.addItem(any(ItemDto.class), any(UserDetailsImpl.class))).thenThrow(itemDuplicateException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value(itemDuplicateException.getLocalizedMessage()));

        verify(itemService, times(1)).addItem(any(ItemDto.class), any(UserDetailsImpl.class));
    }

    @Test
    void sort_successFlow() throws Exception {
        Page page = new PageImpl<>(Collections.singletonList(itemDto));

        when(itemService.findSortedItems(any(PageRequest.class), any(UserDetailsImpl.class))).thenReturn(page);

        mockMvc.perform(get("/items?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize() +
            "&sort=" + pageable.getSort().toString())
             .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.numberOfElements").value(page.getTotalElements()));

        verify(itemService, times(1))
            .findSortedItems(any(PageRequest.class), any(UserDetailsImpl.class));
    }

    @Test
    void findById_successFlow() throws Exception {
        when(itemService.findById(any(Long.class), any(UserDetailsImpl.class))).thenReturn(itemDto);

        mockMvc.perform(get("/items/" + itemDto.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(itemDto.getId()))
            .andExpect(jsonPath("$.accountId").value(itemDto.getAccountId()))
            .andExpect(jsonPath("$.name").value(itemDto.getName()))
            .andExpect(jsonPath("$.unit").value(itemDto.getUnit()))
            .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
            .andExpect(jsonPath("$.volume").value(itemDto.getVolume()))
            .andExpect(jsonPath("$.active").value(itemDto.isActive()));

        verify(itemService, times(1)).findById(eq(itemDto.getId()), any(UserDetailsImpl.class));
    }

    @Test
    void findById_failFlow() throws Exception {
        when(itemService.findById(any(Long.class), any(UserDetailsImpl.class))).thenThrow(itemNotFoundException);

        mockMvc.perform(get("/items/" + itemDto.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(itemNotFoundException.getLocalizedMessage()));

        verify(itemService, times(1)).findById(eq(itemDto.getId()), any(UserDetailsImpl.class));
    }

    @Test
    void softDelete_successFlow() throws Exception{
        when(itemService.softDelete(anyLong(), any(UserDetailsImpl.class))).thenReturn(true);

        mockMvc.perform(delete("/items/" + itemDto.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        verify(itemService, times(1)).softDelete(eq(itemDto.getId()), any(UserDetailsImpl.class));
    }

    @Test
    void softDelete_failFlow() throws Exception{
        when(itemService.softDelete(anyLong(), any(UserDetailsImpl.class))).thenThrow(itemNotFoundException);

        mockMvc.perform(delete("/items/" + itemDto.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(itemNotFoundException.getLocalizedMessage()));

        verify(itemService, times(1)).softDelete(eq(itemDto.getId()), any(UserDetailsImpl.class));
    }

    @Test
    void findItemsByNameQuery_successFlow() throws Exception {
        String query = "Apple";
        List<ItemDto> itemDtoList = List.of(itemDto, itemDto2);

        when(itemService.findItemsByNameQuery(anyString(), any(UserDetailsImpl.class))).thenReturn(itemDtoList);

        mockMvc.perform(get("/items/name?q=" + query)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0]").value(itemDtoList.get(0)))
            .andExpect(jsonPath("$.[1]").value(itemDtoList.get(1)));

        verify(itemService, times(1)).findItemsByNameQuery(eq(query), any(UserDetailsImpl.class));

    }

    @Test
    void updateItem_successFlow() throws Exception{
        when(itemService.updateItem(any(ItemDto.class), any(UserDetailsImpl.class))).thenReturn(itemDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(put("/items")
        .contentType(MediaType.APPLICATION_JSON)
        .content(resultJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemDto.getId()))
        .andExpect(jsonPath("$.accountId").value(itemDto.getAccountId()))
        .andExpect(jsonPath("$.name").value(itemDto.getName()))
        .andExpect(jsonPath("$.unit").value(itemDto.getUnit()))
        .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
        .andExpect(jsonPath("$.volume").value(itemDto.getVolume()))
        .andExpect(jsonPath("$.active").value(itemDto.isActive()));

        verify(itemService, times(1)).updateItem(eq(itemDto), any(UserDetailsImpl.class));
    }

    @Test
    void updateItem_failFlow() throws Exception{
        when(itemService.updateItem(any(ItemDto.class), any(UserDetailsImpl.class))).thenThrow(itemNotFoundException);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(put("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value(itemNotFoundException.getLocalizedMessage()));

        verify(itemService, times(1)).updateItem(eq(itemDto), any(UserDetailsImpl.class));
    }
}
