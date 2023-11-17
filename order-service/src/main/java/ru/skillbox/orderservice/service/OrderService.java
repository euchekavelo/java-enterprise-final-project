package ru.skillbox.orderservice.service;

import ru.skillbox.orderservice.dto.OrderServiceDto;
import ru.skillbox.orderservice.exception.OrderNotFoundException;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.dto.StatusDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface OrderService {

    Order addOrder(OrderServiceDto orderDto, HttpServletRequest request);

    void updateOrderStatus(Long id, StatusDto statusDto) throws OrderNotFoundException;
}
