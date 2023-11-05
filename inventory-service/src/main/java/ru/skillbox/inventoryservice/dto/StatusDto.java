package ru.skillbox.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.skillbox.inventoryservice.dto.enums.OrderStatus;
import ru.skillbox.inventoryservice.dto.enums.ServiceName;

@Data
@AllArgsConstructor
public class StatusDto {

    private OrderStatus status;
    private ServiceName serviceName;
    private String comment;
}
