package com.github.mrchcat.front.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontCurrencies {
    private static final List<BankFrontCurrency> frontCurrencies = new ArrayList<>();
    private static final Map<String, Boolean> accountsMap = new HashMap<>();

    static {
        frontCurrencies.addAll(Arrays.asList(BankFrontCurrency.values()));
    }

    public static List<BankFrontCurrency> getCurrencyList() {
        return frontCurrencies;
    }

    public static Map<String, Boolean> getaccountsMap() {
        accountsMap.clear();
        frontCurrencies.forEach(currency -> accountsMap.put(currency.name(), false));
        return accountsMap;
    }


    public enum BankFrontCurrency {
        RUB("рубли"),
        USD("доллары"),
        CNY("юани");

        public final String title;

        BankFrontCurrency(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
