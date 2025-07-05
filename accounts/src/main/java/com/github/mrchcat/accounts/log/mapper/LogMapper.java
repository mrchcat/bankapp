package com.github.mrchcat.accounts.log.mapper;

import com.github.mrchcat.accounts.account.dto.CashTransactionDto;
import com.github.mrchcat.accounts.account.model.TransactionStatus;
import com.github.mrchcat.accounts.log.model.TransactionLogRecord;

public class LogMapper {

    public static TransactionLogRecord toCashLogRecord(CashTransactionDto cashTransactionDto) {
        return switch (cashTransactionDto.action()) {
            case DEPOSIT -> toCashDepositLogRecord(cashTransactionDto);
            case WITHDRAWAL -> toCashWithdrawalLogRecord(cashTransactionDto);
        };
    }

    public static TransactionLogRecord toCashLogRecord(CashTransactionDto cashTransactionDto, TransactionStatus status) {
        return switch (cashTransactionDto.action()) {
            case DEPOSIT -> toCashDepositLogRecord(cashTransactionDto, status);
            case WITHDRAWAL -> toCashWithdrawalLogRecord(cashTransactionDto, status);
        };
    }


    private static TransactionLogRecord toCashDepositLogRecord(CashTransactionDto cashTransactionDto, TransactionStatus status) {
        TransactionLogRecord record = toCashDepositLogRecord(cashTransactionDto);
        record.setStatus(status);
        return record;
    }

    private static TransactionLogRecord toCashDepositLogRecord(CashTransactionDto cashTransactionDto) {
        return TransactionLogRecord.builder()
                .transactionId(cashTransactionDto.transactionId())
                .action(cashTransactionDto.action())
                .status(cashTransactionDto.status())
                .toAccountId(cashTransactionDto.accountId())
                .amountTo(cashTransactionDto.amount())
                .build();
    }

    private static TransactionLogRecord toCashWithdrawalLogRecord(CashTransactionDto cashTransactionDto) {
        return TransactionLogRecord.builder()
                .transactionId(cashTransactionDto.transactionId())
                .action(cashTransactionDto.action())
                .status(cashTransactionDto.status())
                .fromAccountId(cashTransactionDto.accountId())
                .amountFrom(cashTransactionDto.amount())
                .build();
    }

    private static TransactionLogRecord toCashWithdrawalLogRecord(CashTransactionDto cashTransactionDto, TransactionStatus status) {
        TransactionLogRecord record = toCashWithdrawalLogRecord(cashTransactionDto);
        record.setStatus(status);
        return record;
    }

}
