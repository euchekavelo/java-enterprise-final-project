package ru.skillbox.inventoryservice.service;

import ru.skillbox.inventoryservice.dto.InventoryDto;
import ru.skillbox.inventoryservice.dto.InventoryKafkaDto;
import ru.skillbox.inventoryservice.model.Inventory;

public interface InventoryService {

    void completeOrderInventory(InventoryKafkaDto inventoryKafkaDto);

    Inventory createInventory(InventoryDto inventoryDto, Long userId);
}
