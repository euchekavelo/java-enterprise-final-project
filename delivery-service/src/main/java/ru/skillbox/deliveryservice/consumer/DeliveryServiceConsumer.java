package ru.skillbox.deliveryservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.deliveryservice.dto.DeliveryKafkaDto;
import ru.skillbox.deliveryservice.service.DeliveryService;

@Component
public class DeliveryServiceConsumer {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryServiceConsumer(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @KafkaListener(topics = "delivery")
    public void consumeFromInventoryService(DeliveryKafkaDto deliveryKafkaDto) {
        System.out.println(deliveryKafkaDto);
        deliveryService.makeDelivery(deliveryKafkaDto);
    }
}
