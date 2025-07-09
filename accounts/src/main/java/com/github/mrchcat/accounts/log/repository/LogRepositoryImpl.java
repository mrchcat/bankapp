package com.github.mrchcat.accounts.log.repository;

import com.github.mrchcat.accounts.account.model.TransactionStatus;
import com.github.mrchcat.accounts.log.model.TransactionLogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LogRepositoryImpl implements LogRepository {
    private final JdbcTemplate jdbc;

    @Override
    public Boolean existByTransaction(UUID transactionId, TransactionStatus status) {
        String query = """
                SELECT EXISTS(
                SELECT id
                FROM log
                WHERE transaction_id=? AND status=CAST(? AS transaction_status));
                """;
        return jdbc.queryForObject(query, Boolean.class, transactionId, status.name());
    }

    @Override
    public void create(TransactionLogRecord record) {
        String query = """
                INSERT INTO log(transaction_id,action,status,from_account_id,to_account_id,amount_from,amount_to)
                VALUES (?, CAST(? AS action_type), CAST(? AS transaction_status),?,?,?,?)
                """;
        jdbc.update(query, ps -> {
                    ps.setObject(1, record.getTransactionId());
                    ps.setString(2, record.getAction().name());
                    ps.setString(3, record.getStatus().name());
                    ps.setObject(4, record.getFromAccountId());
                    ps.setObject(5, record.getToAccountId());
                    ps.setBigDecimal(6, record.getAmountFrom());
                    ps.setBigDecimal(7, record.getAmountTo());
                }
        );
    }

    @Override
    public boolean isCorrectStep(UUID transactionId, List<TransactionStatus> statuses) {
        String query = """
                SELECT status
                FROM log
                WHERE transaction_id=?
                """;
        List<TransactionStatus> existingStatuses = jdbc.queryForList(query, TransactionStatus.class, transactionId);
        if (statuses.isEmpty() && existingStatuses.isEmpty()) {
            return true;
        }
        for (TransactionStatus s : statuses) {
            if (existingStatuses.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
