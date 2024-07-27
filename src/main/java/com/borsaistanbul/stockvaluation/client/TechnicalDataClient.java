package com.borsaistanbul.stockvaluation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TechnicalDataClient", url = "http://localhost:8090")
public interface TechnicalDataClient {

    @GetMapping(path = "/price")
    ResponseEntity<GetCurrentPriceResponse> getCurrentPrice(@RequestParam String ticker);

}