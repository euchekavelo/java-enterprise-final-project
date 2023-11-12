package ru.skillbox.deliveryservice.dto;

import lombok.Data;

@Data
public class DeliveryKafkaDto {

    private Long invoiceId;
    private Long orderId;
    private String destinationAddress;
    private String authHeaderValue;
}
