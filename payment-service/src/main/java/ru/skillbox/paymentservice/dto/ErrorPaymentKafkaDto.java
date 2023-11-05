package ru.skillbox.paymentservice.dto;

import lombok.Data;

@Data
public class ErrorPaymentKafkaDto {

    private Long transactionId;
    private Long orderId;
    private StatusDto statusDto;
}
