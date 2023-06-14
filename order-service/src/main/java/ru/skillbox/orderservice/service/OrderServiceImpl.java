package ru.skillbox.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.orderservice.exception.OrderNotFoundException;
import ru.skillbox.orderservice.dto.*;
import ru.skillbox.orderservice.exception.ProductNotFoundException;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.model.Product;
import ru.skillbox.orderservice.model.enums.OrderStatus;
import ru.skillbox.orderservice.model.enums.ServiceName;
import ru.skillbox.orderservice.repository.OrderRepository;
import ru.skillbox.orderservice.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final KafkaService kafkaService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository,
                            KafkaService kafkaService) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.kafkaService = kafkaService;
    }

    @Transactional
    @Override
    public Order addOrder(OrderDto orderDto, Long userId) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(orderDto.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Товара с указанным идентификатором не существует в базе данных!");
        }

        LocalDateTime dateCreation = LocalDateTime.now();
        Order newOrder = new Order();
        newOrder.setDepartureAddress(orderDto.getDepartureAddress());
        newOrder.setDestinationAddress(orderDto.getDestinationAddress());
        newOrder.setDescription(orderDto.getDescription());
        newOrder.setCost(orderDto.getCost());
        newOrder.setUserId(userId);
        newOrder.setProductId(orderDto.getProductId());
        newOrder.setStatus(OrderStatus.REGISTERED);
        newOrder.addStatusHistory(newOrder.getStatus(), ServiceName.ORDER_SERVICE, "Order created");
        newOrder.setCreationTime(dateCreation);
        newOrder.setModifiedTime(dateCreation);
        Order order = orderRepository.save(newOrder);

        kafkaService.produce(OrderKafkaDto.toKafkaDto(order));
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
            log.info("Request with same status {} for order {} from service {}", statusDto.getStatus(), id, statusDto.getServiceName());
            return;
        }
        order.setStatus(statusDto.getStatus());
        order.addStatusHistory(statusDto.getStatus(), statusDto.getServiceName(), statusDto.getComment());
        Order resultOrder = orderRepository.save(order);
        kafkaService.produce(OrderKafkaDto.toKafkaDto(resultOrder));
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
