package ru.skillbox.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.orderservice.dto.ErrorDto;
import ru.skillbox.orderservice.exception.ProductNotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> exceptionHandler(ProductNotFoundException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorDto);
    }
}
