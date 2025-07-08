package com.github.mrchcat.transfer.repository;

import com.github.mrchcat.transfer.mapper.TransferMapper;
import com.github.mrchcat.transfer.model.TransactionStatus;
import com.github.mrchcat.transfer.model.TransferTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferRepositoryImpl implements TransferRepository {

    private final JdbcTemplate jdbc;
    private final TransferRowMapper transferRowMapper;

    @Override
    public TransferTransaction createNewTransaction(TransferTransaction transaction) throws SQLException {
        String query = """
                INSERT INTO transfers(from_account,to_account,from_amount,to_amount,exchange_rate,status,updated_at)
                VALUES (?,?,?,?,?,CAST(? AS transaction_status),NOW())
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, transaction.getFromAccount());
            ps.setObject(2, transaction.getToAccount());
            ps.setBigDecimal(3, transaction.getFromAmount());
            ps.setBigDecimal(4, transaction.getToAmount());
            ps.setBigDecimal(5, transaction.getExchangeRate());
            ps.setString(6, transaction.getStatus().name());
            return ps;
        }, keyHolder);
        Long id = (Long) keyHolder.getKeys().get("id");
        if (id == null) {
            throw new SQLException("не получен ключ для новой записи");
        }
        return getById(id);
    }

    private TransferTransaction getById(long id) {
        String query = """
                SELECT *
                FROM transfers
                WHERE id=?
                """;
        return jdbc.queryForObject(query,transferRowMapper,id);
    }

    @Override
    public void changeTransactionStatus(long id, TransactionStatus status) {
        String query = """
                UPDATE transfers
                SET status=CAST(? AS transaction_status)
                WHERE id=?
                """;
        jdbc.update(query, status.name(), id);
    }
}
