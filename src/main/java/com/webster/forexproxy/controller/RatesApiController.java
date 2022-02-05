package com.webster.forexproxy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.model.Rate;
import com.webster.forexproxy.model.api.ApiResponseBody;
import com.webster.forexproxy.service.RatesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class RatesApiController {

    private final RatesService ratesService;

    @GetMapping("rates")
    public ApiResponseBody<Rate> getRates(@RequestParam("from") String from,
                                          @RequestParam("to") String to) throws Exception {
        final var fromCurrency = Currency.getCurrencyFromString(from);
        final var toCurrency = Currency.getCurrencyFromString(to);
        return ApiResponseBody.success(ratesService.getRates(fromCurrency, toCurrency));
    }
}
