package com.github.mrchcat.accounts.account.repository;

import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.shared.enums.BankCurrency;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Account.builder()
                .id(rs.getObject("id", UUID.class))
                .number(rs.getString("number"))
                .balance(rs.getBigDecimal("balance"))
                .currency(BankCurrency.valueOf(rs.getString("currency_string_code_iso4217")))
                .userId(rs.getObject("user_id", UUID.class))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .isActive(rs.getBoolean("is_active"))
                .build();
    }
}
