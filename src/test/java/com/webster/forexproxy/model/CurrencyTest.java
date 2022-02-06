package com.webster.forexproxy.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.webster.forexproxy.exception.CurrencyNotSupportedException;

public class CurrencyTest {

    @Test
    void testValidCurrencies() throws CurrencyNotSupportedException {
        assertThat(Currency.getCurrencyFromString("AUD")).isEqualTo(Currency.AUD);
        assertThat(Currency.getCurrencyFromString("CAD")).isEqualTo(Currency.CAD);
        assertThat(Currency.getCurrencyFromString("CHF")).isEqualTo(Currency.CHF);
        assertThat(Currency.getCurrencyFromString("CHF")).isEqualTo(Currency.CHF);
        assertThat(Currency.getCurrencyFromString("GBP")).isEqualTo(Currency.GBP);
        assertThat(Currency.getCurrencyFromString("NZD")).isEqualTo(Currency.NZD);
        assertThat(Currency.getCurrencyFromString("JPY")).isEqualTo(Currency.JPY);
        assertThat(Currency.getCurrencyFromString("SGD")).isEqualTo(Currency.SGD);
        assertThat(Currency.getCurrencyFromString("USD")).isEqualTo(Currency.USD);
    }

    @Test
    void testInvalidCurrency() {
        assertThrows(CurrencyNotSupportedException.class, () -> Currency.getCurrencyFromString("KRW"));
        assertThrows(CurrencyNotSupportedException.class, () -> Currency.getCurrencyFromString("SEK"));
        assertThrows(CurrencyNotSupportedException.class, () -> Currency.getCurrencyFromString("MXN"));
    }
}
