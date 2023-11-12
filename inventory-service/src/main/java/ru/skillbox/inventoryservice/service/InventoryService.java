package ru.skillbox.inventoryservice.service;

import ru.skillbox.inventoryservice.dto.CountDto;
import ru.skillbox.inventoryservice.dto.ErrorKafkaDto;
import ru.skillbox.inventoryservice.dto.InventoryDto;
import ru.skillbox.inventoryservice.dto.InventoryKafkaDto;
import ru.skillbox.inventoryservice.exception.InventoryNotFoundException;
import ru.skillbox.inventoryservice.model.Inventory;

public interface InventoryService {

    void completeOrderInventory(InventoryKafkaDto inventoryKafkaDto);

    Inventory createInventory(InventoryDto inventoryDto, Long userId);

    void returnInventory(ErrorKafkaDto errorKafkaDto);

    void replenishInventory(long inventoryId, CountDto countDto) throws InventoryNotFoundException;
}
