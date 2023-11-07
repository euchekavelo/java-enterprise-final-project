package ru.skillbox.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.paymentservice.dto.*;
import ru.skillbox.paymentservice.dto.enums.OrderStatus;
import ru.skillbox.paymentservice.dto.enums.ServiceName;
import ru.skillbox.paymentservice.exception.BalanceNotFoundException;
import ru.skillbox.paymentservice.exception.InsufficientFundsException;
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
        try {
            Thread.sleep(3000);
            Integer cost = paymentKafkaDto.getCost();
            Long userId = paymentKafkaDto.getUserId();
            Optional<Balance> optionalUserBalance = balanceRepository.findBalanceByUserId(userId);
            if (optionalUserBalance.isEmpty()) {
                String comment = "User account not found.";
                sendData(comment, OrderStatus.PAYMENT_FAILED, paymentKafkaDto);
                throw new BalanceNotFoundException(comment);
            }

            Balance userBalance = optionalUserBalance.get();
            Integer balance = userBalance.getBalance();
            if (balance < cost) {
                String comment = "There are not enough funds to make the payment.";
                sendData(comment, OrderStatus.PAYMENT_FAILED, paymentKafkaDto);
                throw new InsufficientFundsException(comment);
            }

            Transaction transaction = new Transaction();
            transaction.setSum(cost);
            transaction.setBalance(userBalance);
            transaction.setTransactionStatus(TransactionStatus.WRITE_OFF);
            Transaction savedTransaction = transactionRepository.save(transaction);

            balance = balance - cost;
            userBalance.setBalance(balance);
            balanceRepository.save(userBalance);

            String comment = "The account balance has been successfully changed.";
            sendData(comment, OrderStatus.PAID, paymentKafkaDto);
            kafkaService.produce(createInventoryKafkaDto(savedTransaction.getId(), paymentKafkaDto));

        } catch (Exception ex) {
            if (!(ex instanceof BalanceNotFoundException) && !(ex instanceof InsufficientFundsException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(createErrorOrderKafkaDto(paymentKafkaDto.getOrderId(), statusDto));
            }

            throw new RuntimeException(ex.getMessage());
        }
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
        inventoryKafkaDto.setDestinationAddress(paymentKafkaDto.getDestinationAddress());
        inventoryKafkaDto.setAuthHeaderValue(paymentKafkaDto.getAuthHeaderValue());
        return inventoryKafkaDto;
    }

    private ErrorOrderKafkaDto createErrorOrderKafkaDto(Long orderId, StatusDto statusDto) {
        ErrorOrderKafkaDto errorOrderKafkaDto = new ErrorOrderKafkaDto();
        errorOrderKafkaDto.setOrderId(orderId);
        errorOrderKafkaDto.setStatusDto(statusDto);
        return errorOrderKafkaDto;
    }

    private void sendData(String comment, OrderStatus orderStatus, PaymentKafkaDto paymentKafkaDto) {
        StatusDto statusDto = createStatusDto(orderStatus, comment);
        requestSendingService.updateOrderStatusInOrderService(paymentKafkaDto.getOrderId(), statusDto,
                paymentKafkaDto.getAuthHeaderValue());
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.PAYMENT_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }
}
