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
        Map<Long, Integer> orderDtoMap = inventoryKafkaDto.getOrderDtoList()
                .stream()
                .collect(Collectors.toMap(OrderDto::getProductId, OrderDto::getCount));

        List<Inventory> inventoryList = inventoryRepository.findInventoryByIdIn(orderDtoMap.keySet());
        Map<Long, Inventory> inventoryMap = inventoryList
                .stream()
                .collect(Collectors.toMap(Inventory::getId, Function.identity()));

        Invoice invoice = new Invoice();
        invoice.setUserId(inventoryKafkaDto.getUserId());
        Invoice savedInvoice = invoiceRepository.save(invoice);

        for (Map.Entry<Long, Integer> entry : orderDtoMap.entrySet()) {
            Long productIdFromDto = entry.getKey();
            if (!inventoryMap.containsKey(productIdFromDto)) {
                String message = "Inventory with ID " + productIdFromDto + " was not found.";
                sendErrorData(message, inventoryKafkaDto);
                throw new InventoryNotFoundException(message);
            }

            Integer countProductFromDto = entry.getValue();
            Inventory inventory = inventoryMap.get(productIdFromDto);
            Integer countProductFromDatabase = inventory.getCount();
            if (countProductFromDatabase < countProductFromDto) {
                String message = "Insufficient inventory with ID " + productIdFromDto;
                sendErrorData(message, inventoryKafkaDto);
                throw new NotEnoughInventoryException(message);
            }

            countProductFromDatabase = countProductFromDatabase - countProductFromDto;
            inventory.setCount(countProductFromDatabase);
            inventoryRepository.save(inventory);

            recordChangesInTheInvoice(inventory, savedInvoice, countProductFromDto);
        }

        String message = "The order has been completed successfully.";
        sendSuccessData(invoice.getId(), inventoryKafkaDto, message);
    }

    private void sendSuccessData(Long invoiceId, InventoryKafkaDto inventoryKafkaDto, String comment) {
        StatusDto statusDto = new StatusDto(OrderStatus.INVENTED, ServiceName.INVENTORY_SERVICE, comment);
        requestSendingService.updateOrderStatusInOrderService(inventoryKafkaDto.getOrderId(), statusDto,
                inventoryKafkaDto.getAuthHeaderValue());

        kafkaService.produce(createDeliveryKafkaDto(invoiceId, inventoryKafkaDto));
    }

    private DeliveryKafkaDto createDeliveryKafkaDto(Long invoiceId, InventoryKafkaDto inventoryKafkaDto) {
        DeliveryKafkaDto deliveryKafkaDto = new DeliveryKafkaDto();
        deliveryKafkaDto.setOrderId(inventoryKafkaDto.getOrderId());
        deliveryKafkaDto.setUserId(inventoryKafkaDto.getUserId());
        deliveryKafkaDto.setAuthHeaderValue(inventoryKafkaDto.getAuthHeaderValue());
        deliveryKafkaDto.setInvoiceId(invoiceId);

        return deliveryKafkaDto;
    }

    private void sendErrorData(String comment, InventoryKafkaDto inventoryKafkaDto) {
        StatusDto statusDto = new StatusDto(OrderStatus.INVENTMENT_FAILED, ServiceName.INVENTORY_SERVICE, comment);
        requestSendingService.updateOrderStatusInOrderService(inventoryKafkaDto.getOrderId(), statusDto,
                inventoryKafkaDto.getAuthHeaderValue());

        kafkaService.produce(createErrorPaymentKafkaDto(inventoryKafkaDto.getTransactionId(), statusDto));
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
