package ru.skillbox.paymentservice.service;

import ru.skillbox.paymentservice.dto.OrderKafkaDto;

public interface PaymentService {
    void pay(OrderKafkaDto orderKafkaDto);
}
