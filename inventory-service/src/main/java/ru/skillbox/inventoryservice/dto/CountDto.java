package ru.skillbox.inventoryservice.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class CountDto {

    @Min(1)
    private Integer count;
}
