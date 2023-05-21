package ru.skillbox.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends Exception {
    public OrderNotFoundException(Long orderId) {
        super("Could not find order '" + orderId + "'.");
    }
}
