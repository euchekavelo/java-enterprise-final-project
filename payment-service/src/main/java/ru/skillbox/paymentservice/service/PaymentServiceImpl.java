package ru.skillbox.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.paymentservice.dto.*;
import ru.skillbox.paymentservice.dto.enums.OrderStatus;
import ru.skillbox.paymentservice.dto.enums.ServiceName;
import ru.skillbox.paymentservice.exception.TransactionNotFoundException;
import ru.skillbox.paymentservice.model.Balance;
import ru.skillbox.paymentservice.model.Transaction;
import ru.skillbox.paymentservice.model.enums.TransactionStatus;
import ru.skillbox.paymentservice.repository.BalanceRepository;
import ru.skillbox.paymentservice.repository.TransactionRepository;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaService kafkaService;
    private final RequestSendingService requestSendingService;

    @Autowired
    public PaymentServiceImpl(BalanceRepository balanceRepository,TransactionRepository transactionRepository,
                              KafkaService kafkaService, RequestSendingService requestSendingService) {

        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
        this.kafkaService = kafkaService;
        this.requestSendingService = requestSendingService;
    }

    @Transactional
    @Override
    public void pay(PaymentKafkaDto paymentKafkaDto) {
        Integer cost = paymentKafkaDto.getCost();
        Long userId = paymentKafkaDto.getUserId();
        Optional<Balance> optionalUserBalance = balanceRepository.findBalanceByUserId(userId);
        if (optionalUserBalance.isEmpty()) {
            sendOrderProcessingErrorData("User account not found.",
                    paymentKafkaDto);
            return;
        }

        Balance userBalance = optionalUserBalance.get();
        Integer balance = userBalance.getBalance();
        if (balance < cost) {
            sendOrderProcessingErrorData("There are not enough funds " +
                    "to make the payment.", paymentKafkaDto);
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setSum(cost);
        transaction.setBalance(userBalance);
        transaction.setTransactionStatus(TransactionStatus.WRITE_OFF);
        Transaction savedTransaction = transactionRepository.save(transaction);

        balance = balance - cost;
        userBalance.setBalance(balance);
        balanceRepository.save(userBalance);

        sendDataAboutSuccessfulOrderProcessing(paymentKafkaDto, savedTransaction.getId());
    }

    @Transactional
    @Override
    public void resetPayment(ErrorPaymentKafkaDto errorPaymentKafkaDto) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(errorPaymentKafkaDto.getTransactionId());
        if (optionalTransaction.isEmpty()) {
            String message = "Transaction with ID " + errorPaymentKafkaDto.getTransactionId() + " was not found.";
            throw new TransactionNotFoundException(message);
        }

        Transaction transaction = optionalTransaction.get();
        Balance balance = transaction.getBalance();
        balance.setBalance(balance.getBalance() + transaction.getSum());
        transaction.setTransactionStatus(TransactionStatus.RETURN);
        balanceRepository.save(balance);
        transactionRepository.save(transaction);

        ErrorOrderKafkaDto errorOrderKafkaDto
                = createErrorOrderKafkaDto(errorPaymentKafkaDto.getOrderId(), errorPaymentKafkaDto.getStatusDto());
        kafkaService.produce(errorOrderKafkaDto);
    }

    private InventoryKafkaDto createInventoryKafkaDto(Long transactionId, PaymentKafkaDto paymentKafkaDto) {
        InventoryKafkaDto inventoryKafkaDto = new InventoryKafkaDto();
        inventoryKafkaDto.setTransactionId(transactionId);
        inventoryKafkaDto.setOrderDtoList(paymentKafkaDto.getOrderDtoList());
        inventoryKafkaDto.setOrderId(paymentKafkaDto.getOrderId());
        inventoryKafkaDto.setUserId(paymentKafkaDto.getUserId());
        inventoryKafkaDto.setAuthHeaderValue(paymentKafkaDto.getAuthHeaderValue());
        return inventoryKafkaDto;
    }

    private ErrorOrderKafkaDto createErrorOrderKafkaDto(Long orderId, StatusDto statusDto) {
        ErrorOrderKafkaDto errorOrderKafkaDto = new ErrorOrderKafkaDto();
        errorOrderKafkaDto.setOrderId(orderId);
        errorOrderKafkaDto.setStatusDto(statusDto);
        return errorOrderKafkaDto;
    }

    private void sendDataAboutSuccessfulOrderProcessing(PaymentKafkaDto paymentKafkaDto, Long transactionId) {
        StatusDto statusDto = new StatusDto(OrderStatus.PAID, ServiceName.PAYMENT_SERVICE,
                "The account balance has been successfully changed.");
        requestSendingService.updateOrderStatusInOrderService(paymentKafkaDto.getOrderId(), statusDto,
                paymentKafkaDto.getAuthHeaderValue());

        kafkaService.produce(createInventoryKafkaDto(transactionId, paymentKafkaDto));
    }

    private void sendOrderProcessingErrorData(String comment, PaymentKafkaDto paymentKafkaDto) {
        StatusDto statusDto = new StatusDto(OrderStatus.PAYMENT_FAILED, ServiceName.PAYMENT_SERVICE, comment);
        requestSendingService.updateOrderStatusInOrderService(paymentKafkaDto.getOrderId(), statusDto,
                paymentKafkaDto.getAuthHeaderValue());

        kafkaService.produce(createErrorOrderKafkaDto(paymentKafkaDto.getOrderId(), statusDto));
    }
}
