package ru.skillbox.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.paymentservice.dto.enums.OrderStatus;
import ru.skillbox.paymentservice.dto.enums.ServiceName;

@Data
@AllArgsConstructor
public class StatusDto {

    private OrderStatus status;
    private ServiceName serviceName;
    private String comment;
}
