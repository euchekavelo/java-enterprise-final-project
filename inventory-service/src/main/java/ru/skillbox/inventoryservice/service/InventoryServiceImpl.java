package ru.skillbox.inventoryservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.inventoryservice.dto.*;
import ru.skillbox.inventoryservice.dto.enums.OrderStatus;
import ru.skillbox.inventoryservice.dto.enums.ServiceName;
import ru.skillbox.inventoryservice.exception.InventoryNotFoundException;
import ru.skillbox.inventoryservice.exception.NotEnoughInventoryException;
import ru.skillbox.inventoryservice.model.Inventory;
import ru.skillbox.inventoryservice.model.Invoice;
import ru.skillbox.inventoryservice.model.InvoiceInventory;
import ru.skillbox.inventoryservice.model.InvoiceInventoryKey;
import ru.skillbox.inventoryservice.repository.InventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceInventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceInventoryRepository invoiceInventoryRepository;
    private final RequestSendingService requestSendingService;
    private final KafkaService kafkaService;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, InvoiceRepository invoiceRepository,
                                InvoiceInventoryRepository invoiceInventoryRepository,
                                RequestSendingService requestSendingService, KafkaService kafkaService) {

        this.inventoryRepository = inventoryRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceInventoryRepository = invoiceInventoryRepository;
        this.requestSendingService = requestSendingService;
        this.kafkaService = kafkaService;
    }

    @Transactional
    @Override
    public void completeOrderInventory(InventoryKafkaDto inventoryKafkaDto) {
        try {
            Map<Long, Integer> orderDtoMap = inventoryKafkaDto.getOrderDtoList()
                    .stream()
                    .collect(Collectors.toMap(OrderDto::getProductId, OrderDto::getCount));

            Map<Long, Inventory> inventoryMap = inventoryRepository.findInventoryByIdIn(orderDtoMap.keySet())
                    .stream()
                    .collect(Collectors.toMap(Inventory::getId, Function.identity()));

            Invoice invoice = new Invoice();
            invoice.setUserId(inventoryKafkaDto.getUserId());
            Invoice savedInvoice = invoiceRepository.save(invoice);

            for (Map.Entry<Long, Integer> entry : orderDtoMap.entrySet()) {
                Long productIdFromDto = entry.getKey();
                if (!inventoryMap.containsKey(productIdFromDto)) {
                    String comment = "Inventory with ID " + productIdFromDto + " was not found.";
                    sendData(comment, OrderStatus.INVENTMENT_FAILED, inventoryKafkaDto);
                    throw new InventoryNotFoundException(comment);
                }

                Integer countProductFromDto = entry.getValue();
                Inventory inventory = inventoryMap.get(productIdFromDto);
                Integer countProductFromDatabase = inventory.getCount();
                if (countProductFromDatabase < countProductFromDto) {
                    String comment = "Insufficient inventory with ID " + productIdFromDto;
                    sendData(comment, OrderStatus.INVENTMENT_FAILED, inventoryKafkaDto);
                    throw new NotEnoughInventoryException(comment);
                }

                countProductFromDatabase = countProductFromDatabase - countProductFromDto;
                inventory.setCount(countProductFromDatabase);
                inventoryRepository.save(inventory);

                recordChangesInTheInvoice(inventory, savedInvoice, countProductFromDto);
            }

            String comment = "The order has been completed successfully.";
            sendData(comment, OrderStatus.INVENTED, inventoryKafkaDto);
            kafkaService.produce(createDeliveryKafkaDto(savedInvoice.getId(), inventoryKafkaDto));

        } catch (Exception ex) {
            if (!(ex instanceof InventoryNotFoundException) && !(ex instanceof NotEnoughInventoryException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(createErrorPaymentKafkaDto(inventoryKafkaDto.getOrderId(), statusDto));
            }

            throw new RuntimeException(ex.getMessage());
        }
    }

    private DeliveryKafkaDto createDeliveryKafkaDto(Long invoiceId, InventoryKafkaDto inventoryKafkaDto) {
        DeliveryKafkaDto deliveryKafkaDto = new DeliveryKafkaDto();
        deliveryKafkaDto.setOrderId(inventoryKafkaDto.getOrderId());
        deliveryKafkaDto.setUserId(inventoryKafkaDto.getUserId());
        deliveryKafkaDto.setDestinationAddress(inventoryKafkaDto.getDestinationAddress());
        deliveryKafkaDto.setAuthHeaderValue(inventoryKafkaDto.getAuthHeaderValue());
        deliveryKafkaDto.setInvoiceId(invoiceId);

        return deliveryKafkaDto;
    }

    private void sendData(String comment, OrderStatus orderStatus, InventoryKafkaDto inventoryKafkaDto) {
        StatusDto statusDto = createStatusDto(orderStatus, comment);
        requestSendingService.updateOrderStatusInOrderService(inventoryKafkaDto.getOrderId(), statusDto,
                inventoryKafkaDto.getAuthHeaderValue());
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.INVENTORY_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }

    private ErrorPaymentKafkaDto createErrorPaymentKafkaDto(Long transactionId, StatusDto statusDto) {
        ErrorPaymentKafkaDto errorPaymentKafkaDto = new ErrorPaymentKafkaDto();
        errorPaymentKafkaDto.setTransactionId(transactionId);
        errorPaymentKafkaDto.setStatusDto(statusDto);

        return errorPaymentKafkaDto;
    }

    private void recordChangesInTheInvoice(Inventory inventory, Invoice invoice, Integer count) {
        InvoiceInventoryKey invoiceInventoryKey = new InvoiceInventoryKey();
        invoiceInventoryKey.setInventoryId(inventory.getId());
        invoiceInventoryKey.setInvoiceId(invoice.getId());

        InvoiceInventory invoiceInventory = new InvoiceInventory();
        invoiceInventory.setInvoiceInventoryKey(invoiceInventoryKey);
        invoiceInventory.setInventory(inventory);
        invoiceInventory.setInvoice(invoice);
        invoiceInventory.setCount(count);
        invoiceInventoryRepository.save(invoiceInventory);
    }

    @Override
    public Inventory createInventory(InventoryDto inventoryDto, Long userId) {
        Inventory inventory = new Inventory();
        inventory.setTitle(inventoryDto.getTitle());
        inventory.setCostPerPiece(inventoryDto.getCostPerPiece());
        inventory.setCount(inventoryDto.getCount());
        inventory.setUserId(userId);

        return inventoryRepository.save(inventory);
    }
}
