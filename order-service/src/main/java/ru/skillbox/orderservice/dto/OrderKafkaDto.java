package ru.skillbox.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.skillbox.orderservice.model.Order;

@AllArgsConstructor
@Data
public class OrderKafkaDto {

    private Long orderId;

    private Long userId;

    private String status;

    private String creationTime;

    private String modifiedTime;

    public static OrderKafkaDto toKafkaDto(Order order) {

        return new OrderKafkaDto(
                order.getId(),
                order.getUserId(),
                order.getStatus().toString(),
                order.getCreationTime().toString(),
                order.getModifiedTime().toString()
        );

    }
}
