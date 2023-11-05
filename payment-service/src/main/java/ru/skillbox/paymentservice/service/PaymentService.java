package ru.skillbox.paymentservice.service;

import ru.skillbox.paymentservice.dto.ErrorPaymentKafkaDto;
import ru.skillbox.paymentservice.dto.PaymentKafkaDto;

public interface PaymentService {

    void pay(PaymentKafkaDto paymentKafkaDto);

    void resetPayment(ErrorPaymentKafkaDto errorPaymentKafkaDto);
}
