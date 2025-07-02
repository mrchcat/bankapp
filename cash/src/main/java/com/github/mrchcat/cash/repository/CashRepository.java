package com.github.mrchcat.cash.repository;

import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.cash.model.TransactionStatus;

public interface CashRepository {

    CashTransaction createNewTransaction(CashTransaction cashTransaction);

    void changeTransactionStatus(long id, TransactionStatus newStatus);


}
