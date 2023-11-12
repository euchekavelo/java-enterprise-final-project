package ru.skillbox.orderservice.dto;

import lombok.Data;
import ru.skillbox.orderservice.model.enums.OrderStatus;
import ru.skillbox.orderservice.model.enums.ServiceName;

@Data
public class StatusDto {

    private OrderStatus status;
    private ServiceName serviceName;
    private String comment;
}
