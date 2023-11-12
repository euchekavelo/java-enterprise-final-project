package ru.skillbox.orderservice.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class OrderServiceDto {

    @NotEmpty
    private List<OrderDto> orderDtoList;

    @NotNull
    @Min(1)
    private Integer cost;

    @NotBlank
    private String destinationAddress;
}
