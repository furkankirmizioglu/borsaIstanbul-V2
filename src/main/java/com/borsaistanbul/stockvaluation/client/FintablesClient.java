package com.borsaistanbul.stockvaluation.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "financialTablesClient", url = "https://fintables.com")
public interface FintablesClient {

    @GetMapping(value = "/sirketler/{ticker}/finansal-tablolar/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    Response downloadFile(@RequestHeader(value = "User-Agent") String userAgent, @PathVariable("ticker") String ticker, @RequestParam(value = "currency") String currency);
}