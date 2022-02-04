package com.webster.forexproxy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.model.Rate;
import com.webster.forexproxy.oneframe.client.OneFrameRateApiClient;

@Service
public class RatesService {

    private final OneFrameRateApiClient oneFrameRateApiClient;

    public RatesService(OneFrameRateApiClient oneFrameRateApiClient) {
        this.oneFrameRateApiClient = oneFrameRateApiClient;
    }

    /**
     * Fetches the currency rate of from -> to from One Frame and returns response
     * @param from currency
     * @param to currency
     * @return the rate of currencies from -> to
     */
    public Rate getRates(Currency from, Currency to) throws Exception {
        if (from == to) {
            throw new InvalidRatesRequestException();
        }

        final var rates = oneFrameRateApiClient.getRates(List.of(generateRequestParam(from, to)));
        return Rate.of(rates.get(0).getPrice());
    }

    private String generateRequestParam(Currency from, Currency to) {
        return from.toString() + to.toString();
    }
}
