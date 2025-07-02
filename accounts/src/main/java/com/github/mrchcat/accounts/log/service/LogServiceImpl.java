package com.github.mrchcat.accounts.log.service;

import com.github.mrchcat.accounts.log.model.TransactionRecord;
import com.github.mrchcat.accounts.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    @Override
    public void saveTransaction(TransactionRecord record) {
        logRepository.save(record);
    }
}
