package ru.skillbox.orderservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.orderservice.dto.ErrorKafkaDto;
import ru.skillbox.orderservice.dto.OrderKafkaDto;
import ru.skillbox.orderservice.exception.OrderNotFoundException;
import ru.skillbox.orderservice.service.OrderService;

@Component
public class OrderServiceConsumer {

    private final OrderService orderService;

    @Autowired
    public OrderServiceConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order", containerFactory = "kafkaListenerContainerOrderKafkaDtoFactory")
    private void consumeFromDeliveryService(OrderKafkaDto orderKafkaDto) {
        try {
            orderService.updateOrderStatus(orderKafkaDto.getOrderId(), orderKafkaDto.getStatusDto());
        } catch (OrderNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "order-error")
    public void consumeFromPaymentService(ErrorKafkaDto errorKafkaDto) {
        try {
            orderService.updateOrderStatus(errorKafkaDto.getOrderId(), errorKafkaDto.getStatusDto());
        } catch (OrderNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
