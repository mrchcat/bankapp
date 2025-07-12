package com.github.mrchcat.accounts.log.mapper;

import com.github.mrchcat.accounts.account.dto.TransferTransactionDto;
import com.github.mrchcat.accounts.log.model.TransactionLogRecord;
import com.github.mrchcat.shared.accounts.AccountCashTransactionDto;
import com.github.mrchcat.shared.enums.CashAction;
import com.github.mrchcat.shared.enums.TransactionStatus;

public class LogMapper {

    public static TransactionLogRecord toCashLogRecord(AccountCashTransactionDto cashTransactionDto) {
        return switch (cashTransactionDto.action()) {
            case DEPOSIT -> toCashDepositLogRecord(cashTransactionDto);
            case WITHDRAWAL -> toCashWithdrawalLogRecord(cashTransactionDto);
            case TRANSFER -> throw new UnsupportedOperationException();
        };
    }

    public static TransactionLogRecord toCashLogRecord(AccountCashTransactionDto cashTransactionDto, TransactionStatus status) {
        return switch (cashTransactionDto.action()) {
            case DEPOSIT -> toCashDepositLogRecord(cashTransactionDto, status);
            case WITHDRAWAL -> toCashWithdrawalLogRecord(cashTransactionDto, status);
            case TRANSFER -> throw new UnsupportedOperationException();
        };
    }


    private static TransactionLogRecord toCashDepositLogRecord(AccountCashTransactionDto cashTransactionDto, TransactionStatus status) {
        TransactionLogRecord record = toCashDepositLogRecord(cashTransactionDto);
        record.setStatus(status);
        return record;
    }

    private static TransactionLogRecord toCashDepositLogRecord(AccountCashTransactionDto cashTransactionDto) {
        return TransactionLogRecord.builder()
                .transactionId(cashTransactionDto.transactionId())
                .action(cashTransactionDto.action())
                .status(cashTransactionDto.status())
                .toAccountId(cashTransactionDto.accountId())
                .amountTo(cashTransactionDto.amount())
                .build();
    }

    private static TransactionLogRecord toCashWithdrawalLogRecord(AccountCashTransactionDto cashTransactionDto) {
        return TransactionLogRecord.builder()
                .transactionId(cashTransactionDto.transactionId())
                .action(cashTransactionDto.action())
                .status(cashTransactionDto.status())
                .fromAccountId(cashTransactionDto.accountId())
                .amountFrom(cashTransactionDto.amount())
                .build();
    }

    private static TransactionLogRecord toCashWithdrawalLogRecord(AccountCashTransactionDto cashTransactionDto, TransactionStatus status) {
        TransactionLogRecord record = toCashWithdrawalLogRecord(cashTransactionDto);
        record.setStatus(status);
        return record;
    }

    public static TransactionLogRecord toNonCashLogRecord(TransferTransactionDto transaction) {
        return TransactionLogRecord.builder()
                .transactionId(transaction.transactionId())
                .action(CashAction.TRANSFER)
                .status(transaction.status())
                .fromAccountId(transaction.fromAccount())
                .toAccountId(transaction.toAccount())
                .amountFrom(transaction.fromAmount())
                .amountTo(transaction.toAmount())
                .build();
    }

    public static TransactionLogRecord toNonCashLogRecord(TransferTransactionDto transaction, TransactionStatus status) {
        TransactionLogRecord record = toNonCashLogRecord(transaction);
        record.setStatus(status);
        return record;
    }

}
