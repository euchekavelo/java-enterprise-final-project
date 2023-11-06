package ru.skillbox.deliveryservice.service;

import ru.skillbox.deliveryservice.dto.DeliveryKafkaDto;

public interface DeliveryService {

    void makeDelivery(DeliveryKafkaDto deliveryKafkaDto);
}
