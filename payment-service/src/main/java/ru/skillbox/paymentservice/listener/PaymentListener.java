package ru.skillbox.paymentservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.paymentservice.dto.OrderKafkaDto;
import ru.skillbox.paymentservice.service.PaymentService;

@Component
public class PaymentListener {

    private final PaymentService paymentService;

    @Autowired
    public PaymentListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "order")
    public void orderServiceListener(OrderKafkaDto orderKafkaDto) {
        System.out.println(orderKafkaDto.toString());
        //paymentService.pay();
    }
}
