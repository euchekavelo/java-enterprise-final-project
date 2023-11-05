package ru.skillbox.paymentservice.service;

import ru.skillbox.paymentservice.dto.SumDto;
import ru.skillbox.paymentservice.exception.BalanceNotFoundException;
import ru.skillbox.paymentservice.model.Balance;

public interface BalanceService {

    Balance createBalance(Long userId);

    void replenishBalance(Long balanceId, SumDto sumDto) throws BalanceNotFoundException;
}
