package com.github.mrchcat.accounts.log.repository;

import com.github.mrchcat.accounts.log.model.TransactionLogRecord;
import com.github.mrchcat.shared.enums.TransactionStatus;

import java.util.List;
import java.util.UUID;

public interface LogRepository {
    Boolean existByTransaction(UUID transactionId, TransactionStatus status);

    void create(TransactionLogRecord record);

    boolean isCorrectStep(UUID transactionId, List<TransactionStatus> statuses);
}
