package com.github.mrchcat.exchange.repository;

import com.github.mrchcat.exchange.model.CurrencyExchangeRecord;
import com.github.mrchcat.shared.enums.BankCurrency;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CurrencyExchangeRecordMapper implements RowMapper<CurrencyExchangeRecord> {
    @Override
    public CurrencyExchangeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CurrencyExchangeRecord.builder()
                .id(rs.getLong("id"))
                .baseCurrency(BankCurrency.valueOf(rs.getString("base")))
                .exchangeCurrency(BankCurrency.valueOf(rs.getString("exchange")))
                .buyRate(rs.getBigDecimal("buy_rate"))
                .sellRate(rs.getBigDecimal("sell_rate"))
                .time(rs.getTimestamp("time").toLocalDateTime())
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
