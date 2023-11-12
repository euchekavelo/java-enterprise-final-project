package ru.skillbox.orderservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDto {

    private String errorMessage;
    private LocalDateTime timestamp;
}
