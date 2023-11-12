package ru.skillbox.paymentservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.paymentservice.dto.ErrorKafkaDto;
import ru.skillbox.paymentservice.dto.PaymentKafkaDto;
import ru.skillbox.paymentservice.service.PaymentService;

@Component
public class PaymentServiceConsumer {

    private final PaymentService paymentService;

    @Autowired
    public PaymentServiceConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "payment")
    public void consumeFromOrderService(PaymentKafkaDto paymentKafkaDto) {
        paymentService.pay(paymentKafkaDto);
    }

    @KafkaListener(topics = "payment-error", containerFactory = "kafkaListenerContainerErrorFactory")
    public void consumeFromInventoryService(ErrorKafkaDto errorKafkaDto) {
        paymentService.resetPayment(errorKafkaDto);
    }
}
