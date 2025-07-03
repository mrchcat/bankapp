package com.github.mrchcat.accounts.log.service;

import com.github.mrchcat.accounts.log.model.TransactionLog;

public interface LogService {

    void saveTransaction(TransactionLog record);
}
