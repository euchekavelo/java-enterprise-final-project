package ru.skillbox.paymentservice.service;

import org.springframework.stereotype.Service;
import ru.skillbox.paymentservice.dto.OrderKafkaDto;

@Service
public class PaymentServiceImpl implements PaymentService {


    @Override
    public void pay(OrderKafkaDto orderKafkaDto) {
        Long userId = orderKafkaDto.getUserId();

        if
    }
}
