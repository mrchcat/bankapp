package com.github.mrchcat.accounts.log.repository;

import com.github.mrchcat.accounts.account.model.TransactionStatus;
import com.github.mrchcat.accounts.log.model.TransactionLogRecord;

import java.util.UUID;

public interface LogRepository {
    Boolean existByTransaction(UUID transactionId, TransactionStatus status);

    void create(TransactionLogRecord record);
}
