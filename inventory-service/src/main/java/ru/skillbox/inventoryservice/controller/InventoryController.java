package ru.skillbox.inventoryservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.inventoryservice.dto.CountDto;
import ru.skillbox.inventoryservice.dto.InventoryDto;
import ru.skillbox.inventoryservice.exception.InventoryNotFoundException;
import ru.skillbox.inventoryservice.model.Inventory;
import ru.skillbox.inventoryservice.service.InventoryService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Create a new inventory.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/inventory")
    public ResponseEntity<Inventory> createInventory(@RequestBody InventoryDto inventoryDto, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(inventoryDto, Long.valueOf(request.getHeader("id"))));
    }

    @Operation(summary = "Replenish inventory.", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/inventory/{inventoryId}")
    public ResponseEntity<Void> replenishInventory(@Parameter(description = "Id of delivery") @PathVariable long inventoryId,
                                                   @Valid @RequestBody CountDto countDto) throws InventoryNotFoundException {

        inventoryService.replenishInventory(inventoryId, countDto);
        return ResponseEntity.ok().build();
    }
}
