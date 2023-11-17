package ru.skillbox.orderservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.orderservice.config.TestConfig;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.OrderServiceDto;
import ru.skillbox.orderservice.dto.StatusDto;
import ru.skillbox.orderservice.exception.OrderNotFoundException;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.model.enums.OrderStatus;
import ru.skillbox.orderservice.model.enums.ServiceName;
import ru.skillbox.orderservice.repository.OrderRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void addOrderSuccessTest() {
        OrderDto orderDto = new OrderDto();
        orderDto.setProductId(1L);
        orderDto.setCount(2);
        List<OrderDto> orderDtoList = new ArrayList<>();
        orderDtoList.add(orderDto);

        OrderServiceDto orderServiceDto = new OrderServiceDto();
        orderServiceDto.setOrderDtoList(orderDtoList);
        orderServiceDto.setCost(100);
        orderServiceDto.setDestinationAddress("test address");

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("id", 1L);

        Order order = new Order();
        order.setId(1L);
        order.addProductDetails(new HashMap<>(Collections.singletonMap(1L, 2)));
        order.setCost(100);
        order.setStatus(OrderStatus.REGISTERED);
        order.setDestinationAddress("test address");
        order.addStatusHistory(OrderStatus.REGISTERED, ServiceName.ORDER_SERVICE,
                "The order has been registered.");

        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
        assertThat(orderService.addOrder(orderServiceDto, mockHttpServletRequest).getCost()).isEqualTo(order.getCost());
    }

    @Test
    void updateOrderStatusTestThrowException() {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(OrderStatus.INVENTED);
        statusDto.setComment("Order has been paid.");

        when(orderRepository.findById(2L).stream().findFirst()).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(2L, statusDto));
    }

    @Test
    void updateOrderStatusTestWithoutException() {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(OrderStatus.INVENTED);
        statusDto.setComment("Order has been paid.");

        Order order = new Order();
        order.setId(1L);
        order.addProductDetails(new HashMap<>(Collections.singletonMap(1L, 2)));
        order.setCost(100);
        order.setStatus(OrderStatus.REGISTERED);
        order.setDestinationAddress("test address");
        order.addStatusHistory(OrderStatus.REGISTERED, ServiceName.ORDER_SERVICE,
                "The order has been registered.");

        when(orderRepository.findById(1L).stream().findFirst()).thenReturn(Optional.of(order));
        assertDoesNotThrow(() -> orderService.updateOrderStatus(1L, statusDto));
    }
}