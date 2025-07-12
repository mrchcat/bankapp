package com.github.mrchcat.transfer.repository;

import com.github.mrchcat.shared.enums.TransactionStatus;
import com.github.mrchcat.transfer.model.TransferTransaction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class TransferRowMapper implements RowMapper<TransferTransaction> {
    @Override
    public TransferTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TransferTransaction.builder()
                .id(rs.getLong("id"))
                .transactionId(rs.getObject("transaction_id", UUID.class))
                .fromAccount(rs.getObject("from_account", UUID.class))
                .toAccount(rs.getObject("to_account", UUID.class))
                .fromAmount(rs.getBigDecimal("from_amount"))
                .toAmount(rs.getBigDecimal("to_amount"))
                .exchangeRate(rs.getBigDecimal("exchange_rate"))
                .status(TransactionStatus.valueOf(rs.getString("status")))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}