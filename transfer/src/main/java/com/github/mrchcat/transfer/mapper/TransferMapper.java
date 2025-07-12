package com.github.mrchcat.transfer.mapper;

import com.github.mrchcat.shared.accounts.AccountTransferTransactionDto;
import com.github.mrchcat.transfer.model.TransferTransaction;

public class TransferMapper {

    public static AccountTransferTransactionDto toRequestDto(TransferTransaction transaction) {
        return AccountTransferTransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .fromAccount(transaction.getFromAccount())
                .toAccount(transaction.getToAccount())
                .fromAmount(transaction.getFromAmount())
                .toAmount(transaction.getToAmount())
                .status(transaction.getStatus())
                .build();
    }

}
