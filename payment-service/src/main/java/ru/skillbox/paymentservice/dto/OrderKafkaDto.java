package ru.skillbox.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderKafkaDto {

    private Long orderId;

    private Long userId;

    private String status;

    private String creationTime;

    private String modifiedTime;

    public OrderKafkaDto() {
    }
}
