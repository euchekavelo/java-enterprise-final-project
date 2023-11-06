package ru.skillbox.deliveryservice.dto;

import lombok.Data;

@Data
public class ErrorInventoryKafkaDto {

    private Long invoiceId;
    private StatusDto statusDto;
}
