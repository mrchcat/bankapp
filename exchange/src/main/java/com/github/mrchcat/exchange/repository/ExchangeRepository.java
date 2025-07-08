package com.github.mrchcat.exchange.repository;

import com.github.mrchcat.exchange.model.CurrencyExchangeRecord;

public interface ExchangeRepository {

    void save(CurrencyExchangeRecord record);
}
