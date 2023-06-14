package ru.skillbox.orderservice.service;

import ru.skillbox.orderservice.exception.OrderNotFoundException;
import ru.skillbox.orderservice.exception.ProductNotFoundException;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.StatusDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface OrderService {

    Order addOrder(OrderDto orderDto, Long userId) throws ProductNotFoundException;

    void updateOrderStatus(Long id, StatusDto statusDto) throws OrderNotFoundException;

    List<Order> getOrderList();

    Order getOrder(Long orderId) throws OrderNotFoundException;
}
