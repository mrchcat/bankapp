package com.github.mrchcat.front.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontCurrencies {
    private static final List<BankCurrency> frontCurrencies = new ArrayList<>();
    private static final Map<String,Boolean> accountsMap=new HashMap<>();

    static {
        frontCurrencies.addAll(Arrays.asList(BankCurrency.values()));
    }

    public static List<BankCurrency> getCurrencyList() {
        return frontCurrencies;
    }

    public static Map<String,Boolean> getaccountsMap() {
        accountsMap.clear();
        frontCurrencies.forEach(currency->accountsMap.put(currency.name(),false));
        return accountsMap;
    }


    public enum BankCurrency {
        RUB("рубли"),
        USD("доллары"),
        CNY("юани");

        public final String title;

        BankCurrency(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
