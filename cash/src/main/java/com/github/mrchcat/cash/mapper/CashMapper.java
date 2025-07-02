package com.github.mrchcat.cash.mapper;

import com.github.mrchcat.cash.dto.CashTransactionRequestDto;
import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.cash.model.TransactionStatus;

public class CashMapper {

    public static CashTransactionRequestDto toRequestDto(CashTransaction dto, TransactionStatus status) {
        return CashTransactionRequestDto.builder()
                .transactionId(dto.getTransactionId())
                .account(dto.getAccountId())
                .amount(dto.getAmount())
                .action(dto.getAction())
                .status(status)
                .build();
    }
}
