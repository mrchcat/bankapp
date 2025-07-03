package com.github.mrchcat.accounts.log.service;

import com.github.mrchcat.accounts.log.model.TransactionLog;
import com.github.mrchcat.accounts.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    @Override
    public void saveTransaction(TransactionLog record) {
        logRepository.save(record);
    }
}
