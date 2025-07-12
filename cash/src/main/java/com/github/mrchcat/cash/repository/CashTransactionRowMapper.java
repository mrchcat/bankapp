package com.github.mrchcat.cash.repository;

import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.enums.CashAction;
import com.github.mrchcat.shared.enums.TransactionStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class CashTransactionRowMapper implements RowMapper<CashTransaction> {
    @Override
    public CashTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CashTransaction.builder()
                .id(rs.getLong("id"))
                .transactionId(rs.getObject("transaction_id", UUID.class))
                .action(CashAction.valueOf(rs.getString("action")))
                .userId(rs.getObject("user_id", UUID.class))
                .username(rs.getString("username"))
                .accountId(rs.getObject("account_id", UUID.class))
                .currencyStringCodeIso4217(BankCurrency.valueOf(rs.getString("currency_string_code_iso4217")))
                .amount(rs.getBigDecimal("amount"))
                .status(TransactionStatus.valueOf(rs.getString("status")))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}
