package com.github.mrchcat.exchange.repository;

import com.github.mrchcat.exchange.model.CurrencyExchangeRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
@RequiredArgsConstructor
public class ExchangeRepositoryImpl implements ExchangeRepository {
    private final CurrencyExchangeRecordMapper currencyExchangeRecordMapper;
    private final JdbcTemplate jdbc;


    @Override
    public void save(CurrencyExchangeRecord record) {
        String query = """
                INSERT INTO exchange_rates(base,exchange, rate, time)
                VALUES (CAST(? AS currency),CAST(? AS currency),?,?)
                """;
        jdbc.update(query, ps -> {
            ps.setString(1, record.getBaseCurrency().name());
            ps.setString(2, record.getExchangeCurrency().name());
            ps.setBigDecimal(3, record.getRate());
            ps.setTimestamp(4, Timestamp.valueOf(record.getTime()));
        });

    }
}
