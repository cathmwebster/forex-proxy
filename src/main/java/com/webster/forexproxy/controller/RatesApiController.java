package com.webster.forexproxy.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webster.forexproxy.model.Rate;
import com.webster.forexproxy.model.api.ApiResponseBody;

@RestController
@RequestMapping("/v1")
public class RatesApiController {


    @GetMapping("rates")
    public ApiResponseBody<Rate> getRates(@RequestParam("from") String from,
                                          @RequestParam("to") String to) throws Exception {
        return ApiResponseBody.success(Rate.of(BigDecimal.ONE));
    }
}
