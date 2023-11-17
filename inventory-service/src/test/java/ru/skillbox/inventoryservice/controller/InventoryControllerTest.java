package ru.skillbox.inventoryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.inventoryservice.dto.CountDto;
import ru.skillbox.inventoryservice.dto.InventoryDto;
import ru.skillbox.inventoryservice.exception.InventoryNotFoundException;
import ru.skillbox.inventoryservice.model.Inventory;
import ru.skillbox.inventoryservice.service.InventoryService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Configuration
    @ComponentScan(basePackageClasses = {InventoryController.class})
    public static class TestConf {
    }

    @Test
    void createInventorySuccessTest() throws Exception {
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setCount(200);
        inventoryDto.setTitle("test inventory");
        inventoryDto.setCostPerPiece(2);

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setCount(inventoryDto.getCount());
        inventory.setUserId(1L);
        inventory.setTitle(inventoryDto.getTitle());
        inventory.setCostPerPiece(inventoryDto.getCostPerPiece());

        when(inventoryService.createInventory(inventoryDto, 1L)).thenReturn(inventory);
        mockMvc.perform(
                        post("/inventory")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .content(objectMapper.writeValueAsString(inventoryDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1));
    }

    @Test
    void replenishInventorySuccessTest() throws Exception {
        CountDto countDto = new CountDto();
        countDto.setCount(10);

        doNothing().when(inventoryService).replenishInventory(1L, countDto);
        mockMvc.perform(
                        patch("/inventory/1")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .content(objectMapper.writeValueAsString(countDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void replenishInventoryErrorTest() throws Exception {
        CountDto countDto = new CountDto();
        countDto.setCount(10);

        doThrow(new InventoryNotFoundException("Inventory with ID 2 was not found."))
                .when(inventoryService).replenishInventory(2L, countDto);
        mockMvc.perform(
                        patch("/inventory/2")
                                .with(request -> {
                                    request.addHeader("id", 2);
                                    return request;
                                })
                                .content(objectMapper.writeValueAsString(countDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}