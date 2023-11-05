package ru.skillbox.inventoryservice.dto;

import lombok.Data;

@Data
public class ErrorPaymentKafkaDto {

    private Long transactionId;
    private StatusDto statusDto;
}
