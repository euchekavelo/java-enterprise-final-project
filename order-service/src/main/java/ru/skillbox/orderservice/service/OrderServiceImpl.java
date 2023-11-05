package ru.skillbox.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.orderservice.exception.OrderNotFoundException;
import ru.skillbox.orderservice.dto.*;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.model.enums.OrderStatus;
import ru.skillbox.orderservice.model.enums.ServiceName;
import ru.skillbox.orderservice.repository.OrderRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaService kafkaService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, KafkaService kafkaService) {
        this.orderRepository = orderRepository;
        this.kafkaService = kafkaService;
    }

    @Transactional
    @Override
    public Order addOrder(OrderServiceDto orderServiceDto, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("id"));
        String authHeaderValue = request.getHeader("Authorization");
        HashMap<Long, Integer> quantityProducts = new HashMap<>();
        List<OrderDto> orderDtoList = orderServiceDto.getOrderDtoList();
        orderDtoList.forEach(orderDto -> {
            quantityProducts.merge(orderDto.getProductId(), orderDto.getCount(), Integer::sum);
        });
        orderDtoList.clear();
        quantityProducts.forEach((key, value) -> orderDtoList.add(new OrderDto(key, value)));

        Order newOrder = new Order();
        newOrder.setDestinationAddress(orderServiceDto.getDestinationAddress());
        newOrder.setCost(orderServiceDto.getCost());
        newOrder.setUserId(userId);
        newOrder.addProductDetails(quantityProducts);
        newOrder.setStatus(OrderStatus.REGISTERED);
        newOrder.addStatusHistory(newOrder.getStatus(), ServiceName.ORDER_SERVICE, "Order created");
        Order order = orderRepository.save(newOrder);

        kafkaService.produce(new PaymentKafkaDto(userId, orderDtoList, orderServiceDto.getCost(),
                                order.getId(), authHeaderValue));
        return order;
    }

    @Transactional
    @Override
    public void updateOrderStatus(Long id, StatusDto statusDto) throws OrderNotFoundException {
        Optional<Order> orderOptional = orderRepository.findById(id).stream().findFirst();
        if (orderOptional.isEmpty()) {
            throw new OrderNotFoundException(id);
        }

        Order order = orderOptional.get();
        if (order.getStatus() == statusDto.getStatus()) {
            log.info("Request with same status {} for order {} from service {}", statusDto.getStatus(),
                    id, statusDto.getServiceName());
            return;
        }
        order.setStatus(statusDto.getStatus());
        order.addStatusHistory(statusDto.getStatus(), statusDto.getServiceName(), statusDto.getComment());
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrderList() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrder(Long orderId) throws OrderNotFoundException {
        Optional<Order> orderOptional = orderRepository.findById(orderId).stream().findFirst();
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        } else {
            throw new OrderNotFoundException(orderId);
        }
    }
}
