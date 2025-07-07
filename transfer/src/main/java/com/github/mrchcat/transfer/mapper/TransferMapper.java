package com.github.mrchcat.transfer.mapper;

import com.github.mrchcat.transfer.dto.TransferTransactionRequestDto;
import com.github.mrchcat.transfer.model.TransferTransaction;

public class TransferMapper {

    public static TransferTransactionRequestDto toRequestDto(TransferTransaction transaction) {
        return TransferTransactionRequestDto.builder()
                .transactionId(transaction.getTransactionId())
                .fromAccount(transaction.getFromAccount())
                .toAccount(transaction.getToAccount())
                .fromAmount(transaction.getFromAmount())
                .toAmount(transaction.getToAmount())
                .status(transaction.getStatus())
                .build();
    }

}
