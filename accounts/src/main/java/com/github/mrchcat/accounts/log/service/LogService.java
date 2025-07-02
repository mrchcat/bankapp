package com.github.mrchcat.accounts.log.service;

import com.github.mrchcat.accounts.log.model.TransactionRecord;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;

public interface LogService {

    void saveTransaction(TransactionRecord record);
}
