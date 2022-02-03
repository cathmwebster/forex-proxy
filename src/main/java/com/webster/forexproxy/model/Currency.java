package com.webster.forexproxy.model;

import java.util.Arrays;
import java.util.List;

import com.webster.forexproxy.exception.CurrencyNotSupportedException;

import lombok.Getter;

@Getter
public enum Currency {
    AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD;

    public static List<Currency> getAllValues() {
        return Arrays.asList(values());
    }

    public static Currency getCurrencyFromString(String currency) throws CurrencyNotSupportedException {
        try {
            return Currency.valueOf(currency);
        } catch (Exception e) {
            throw new CurrencyNotSupportedException("CurrencyNotSupportedException: The given currency is not accepted: " + currency);
        }
    }
}
