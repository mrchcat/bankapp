package com.github.mrchcat.accounts.log.service;

import com.github.mrchcat.accounts.log.model.TransactionRecord;

public interface LogService {

    void saveTransaction(TransactionRecord record);
}
