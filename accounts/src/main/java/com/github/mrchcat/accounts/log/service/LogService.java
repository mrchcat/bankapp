package com.github.mrchcat.accounts.log.service;

import com.github.mrchcat.accounts.account.model.TransactionStatus;
import com.github.mrchcat.accounts.log.model.TransactionLogRecord;

import java.util.List;
import java.util.UUID;

public interface LogService {

    void saveTransactionLogRecord(TransactionLogRecord record);

    boolean existByTransaction(UUID transactionId, TransactionStatus status);

    boolean isCorrectStep(UUID transactionId, List<TransactionStatus> statuses);

}

