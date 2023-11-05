package ru.skillbox.inventoryservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.inventoryservice.dto.InventoryDto;
import ru.skillbox.inventoryservice.model.Inventory;
import ru.skillbox.inventoryservice.service.InventoryService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    //@Operation(summary = "Create a user payment account", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Inventory> createPaymentAccount(@RequestBody InventoryDto inventoryDto,
                                                          HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(inventoryDto, Long.valueOf(request.getHeader("id"))));
    }
}
