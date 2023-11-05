package ru.skillbox.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorOrderKafkaDto {

    private Long orderId;
    private StatusDto statusDto;

    public ErrorOrderKafkaDto() {
    }
}
