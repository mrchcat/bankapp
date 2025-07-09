package com.github.mrchcat.cash.repository;

import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.cash.model.TransactionStatus;
import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CashRepositoryImpl implements CashRepository {
    private final JdbcTemplate jdbc;
    private final CashTransactionRowMapper cashTransactionRowMapper;

    @Override
    public CashTransaction createNewTransaction(CashTransaction cashTransaction) {
        String query = """
                INSERT INTO cash_transactions(transaction_id,action,user_id,username,account_id,currency_string_code_iso4217,amount,status,updated_at)
                VALUES (?, CAST(? AS cash_action),?,?,?, CAST(? AS currency),?,CAST('STARTED' AS transaction_status),NOW())
                """;
        jdbc.update(query, ps -> {
            ps.setObject(1, cashTransaction.getTransactionId());
            ps.setString(2, cashTransaction.getAction().name());
            ps.setObject(3, cashTransaction.getUserId());
            ps.setString(4, cashTransaction.getUsername());
            ps.setObject(5, cashTransaction.getAccountId());
            ps.setString(6, cashTransaction.getCurrencyStringCodeIso4217().name());
            ps.setBigDecimal(7, cashTransaction.getAmount());
        });
        return getTransaction(cashTransaction.getTransactionId());
    }

    private CashTransaction getTransaction(UUID transactionId) {
        String query = """
                SELECT *
                FROM cash_transactions
                WHERE transaction_id=?
                """;
        return jdbc.queryForObject(query, cashTransactionRowMapper, transactionId);
    }

    @Override
    public void changeTransactionStatus(long id, TransactionStatus newStatus) {
        String query = """
                UPDATE cash_transactions
                SET status=CAST(? AS transaction_status)
                WHERE id=?
                """;
        int updated = jdbc.update(query, ps -> {
            ps.setString(1, newStatus.name());
            ps.setLong(2, id);
        });
        if (updated == 0) {
            throw new InternalException("Статус транзакции не обновлен");
        }
    }
}
