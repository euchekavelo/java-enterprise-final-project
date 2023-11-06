package ru.skillbox.deliveryservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.deliveryservice.dto.DeliveryKafkaDto;
import ru.skillbox.deliveryservice.dto.ErrorInventoryKafkaDto;
import ru.skillbox.deliveryservice.dto.OrderKafkaDto;
import ru.skillbox.deliveryservice.dto.StatusDto;
import ru.skillbox.deliveryservice.dto.enums.OrderStatus;
import ru.skillbox.deliveryservice.dto.enums.ServiceName;
import ru.skillbox.deliveryservice.exception.FailedDeliveryException;
import ru.skillbox.deliveryservice.model.Delivery;
import ru.skillbox.deliveryservice.model.enums.DeliveryStatus;
import ru.skillbox.deliveryservice.repository.DeliveryRepository;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final KafkaService kafkaService;
    private final RequestSendingService requestSendingService;

    @Autowired
    public DeliveryServiceImpl(DeliveryRepository deliveryRepository, KafkaService kafkaService,
                               RequestSendingService requestSendingService) {

        this.deliveryRepository = deliveryRepository;
        this.kafkaService = kafkaService;
        this.requestSendingService = requestSendingService;
    }

    @Transactional
    @Override
    public void makeDelivery(DeliveryKafkaDto deliveryKafkaDto) {
        try {
            Delivery delivery = new Delivery();
            delivery.setInvoiceId(deliveryKafkaDto.getInvoiceId());
            delivery.setDestinationAddress(deliveryKafkaDto.getDestinationAddress());

            double randomValue = Math.round(Math.random() * 100.0) / 100.0;
            System.out.println("random: " + randomValue);
            if (randomValue > 0.85) {
                delivery.setDeliveryStatus(DeliveryStatus.FAILURE);
                deliveryRepository.save(delivery);
                String comment = "The order delivery was unsuccessful.";
                sendData(comment, OrderStatus.DELIVERY_FAILED, deliveryKafkaDto);
                throw new FailedDeliveryException(comment);
            }

            delivery.setDeliveryStatus(DeliveryStatus.SUCCESS);
            deliveryRepository.save(delivery);
            String comment = "The order delivery was unsuccessful.";
            sendData(comment, OrderStatus.DELIVERED, deliveryKafkaDto);
            kafkaService.produce(createOrderKafkaDto(deliveryKafkaDto.getOrderId()));

        } catch (Exception ex) {
            if (!(ex instanceof FailedDeliveryException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(createErrorInventoryDto(deliveryKafkaDto.getOrderId(), statusDto));
            }
        }
    }

    private void sendData(String comment, OrderStatus orderStatus, DeliveryKafkaDto deliveryKafkaDto) {
        StatusDto statusDto = createStatusDto(orderStatus, comment);
        requestSendingService.updateOrderStatusInOrderService(deliveryKafkaDto.getOrderId(), statusDto,
                deliveryKafkaDto.getAuthHeaderValue());
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.DELIVERY_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }

    private OrderKafkaDto createOrderKafkaDto(Long orderId) {
        OrderKafkaDto orderKafkaDto = new OrderKafkaDto();
        orderKafkaDto.setOrderId(orderId);

        return orderKafkaDto;
    }

    private ErrorInventoryKafkaDto createErrorInventoryDto(Long invoiceId, StatusDto statusDto) {
        ErrorInventoryKafkaDto errorInventoryKafkaDto = new ErrorInventoryKafkaDto();
        errorInventoryKafkaDto.setInvoiceId(invoiceId);
        errorInventoryKafkaDto.setStatusDto(statusDto);

        return errorInventoryKafkaDto;
    }
}
