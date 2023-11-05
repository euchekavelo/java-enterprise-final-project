package ru.skillbox.deliveryservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.deliveryservice.dto.DeliveryKafkaDto;

@Component
public class DeliveryServiceConsumer {

    //private final InventoryService inventoryService;

    public DeliveryServiceConsumer(/*InventoryService inventoryService*/) {
        //this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "delivery")
    public void consumeFromInventoryService(DeliveryKafkaDto deliveryKafkaDto) {
        System.out.println(deliveryKafkaDto);
    }
}
