package com.github.mrchcat.front.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontCurrencies {
    private static final List<BankCurrency> frontCurrencies = new ArrayList<>();

    static {
        frontCurrencies.addAll(Arrays.asList(BankCurrency.values()));
    }

    public static List<BankCurrency> getCurrencyList() {
        return frontCurrencies;
    }

    public enum BankCurrency {
        RUB("рубли"),
        USD("доллары"),
        CNY("юани");

        public final String title;

        BankCurrency(String title) {
            this.title = title;
        }

    }
}
