package com.github.mrchcat.cash.mapper;

import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.shared.accounts.AccountCashTransactionDto;
import com.github.mrchcat.shared.enums.TransactionStatus;

public class CashMapper {

    public static AccountCashTransactionDto toRequestDto(CashTransaction dto, TransactionStatus status) {
        return AccountCashTransactionDto.builder()
                .transactionId(dto.getTransactionId())
                .accountId(dto.getAccountId())
                .amount(dto.getAmount())
                .action(dto.getAction())
                .status(status)
                .build();
    }
}
