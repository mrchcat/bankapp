package com.github.mrchcat.accounts.log.repository;

import com.github.mrchcat.accounts.log.model.TransactionLog;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LogRepository extends CrudRepository<TransactionLog, Long> {

    boolean existsByTransactionId(UUID transactionId);

}
