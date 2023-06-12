package ru.skillbox.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderKafkaDto {

    private Long id;

    private String status;

    private String creationTime;

    private String modifiedTime;

    public OrderKafkaDto() {
    }

    @Override
    public String toString() {
        return "OrderKafkaDto{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", creationTime='" + creationTime + '\'' +
                ", modifiedTime='" + modifiedTime + '\'' +
                '}';
    }
}
