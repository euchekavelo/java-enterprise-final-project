package ru.skillbox.inventoryservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.inventoryservice.dto.ErrorKafkaDto;
import ru.skillbox.inventoryservice.dto.InventoryKafkaDto;
import ru.skillbox.inventoryservice.service.InventoryService;

@Component
public class InventoryServiceConsumer {

    private final InventoryService inventoryService;

    public InventoryServiceConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "inventory")
    public void consumeFromPaymentService(InventoryKafkaDto inventoryKafkaDto) {
        System.out.println(inventoryKafkaDto);
        inventoryService.completeOrderInventory(inventoryKafkaDto);
    }

    @KafkaListener(topics = "inventory-error", containerFactory = "kafkaListenerContainerErrorFactory")
    public void consumeFromDeliveryService(ErrorKafkaDto errorKafkaDto) {
        System.out.println(errorKafkaDto);
        inventoryService.returnInventory(errorKafkaDto);
    }
}
