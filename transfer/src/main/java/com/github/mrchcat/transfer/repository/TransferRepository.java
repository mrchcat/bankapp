package com.github.mrchcat.transfer.repository;

import com.github.mrchcat.shared.enums.TransactionStatus;
import com.github.mrchcat.transfer.model.TransferTransaction;

import java.sql.SQLException;

public interface TransferRepository {

    TransferTransaction createNewTransaction(TransferTransaction transaction) throws SQLException;

    void changeTransactionStatus(long id, TransactionStatus status);
}
