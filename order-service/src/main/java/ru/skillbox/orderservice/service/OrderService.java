package ru.skillbox.orderservice.service;

import ru.skillbox.orderservice.exception.OrderNotFoundException;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.StatusDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Optional<Order> addOrder(OrderDto orderDto);

    void updateOrderStatus(Long id, StatusDto statusDto) throws OrderNotFoundException;

    List<Order> getOrderList();

    Order getOrder(Long orderId) throws OrderNotFoundException;
}
