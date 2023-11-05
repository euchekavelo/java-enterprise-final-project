package ru.skillbox.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryDto {

    private String title;
    private Integer count;
    private Integer costPerPiece;
}
