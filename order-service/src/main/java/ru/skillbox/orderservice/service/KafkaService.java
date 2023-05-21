package ru.skillbox.orderservice.service;

import ru.skillbox.orderservice.dto.OrderKafkaDto;

public interface KafkaService {

    void produce(OrderKafkaDto orderKafkaDto);
}
