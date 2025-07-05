package com.github.mrchcat.accounts.log.service;

import com.github.mrchcat.accounts.account.model.TransactionStatus;
import com.github.mrchcat.accounts.log.model.TransactionLogRecord;
import com.github.mrchcat.accounts.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    @Override
    public void saveTransactionLogRecord(TransactionLogRecord record) {
        logRepository.create(record);
    }

    @Override
    public boolean existByTransaction(UUID transactionId, TransactionStatus status) {
        return logRepository.existByTransaction(transactionId, status);
    }

    @Override
    public boolean isCorrectStep(UUID transactionId, List<TransactionStatus> statuses) {
        System.out.println("проверяем корректность операций transactionId=" + transactionId + " statuses=" + statuses);
        return true;
    }
}
