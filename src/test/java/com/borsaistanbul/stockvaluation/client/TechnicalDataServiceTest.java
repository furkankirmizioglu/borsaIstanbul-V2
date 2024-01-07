package com.borsaistanbul.stockvaluation.client;

import com.borsaistanbul.stockvaluation.exception.StockValuationApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TechnicalDataServiceTest {

    TechnicalDataService technicalDataService;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void init() {
        restTemplate = mock(RestTemplate.class);
        technicalDataService = new TechnicalDataServiceImpl();
    }

    @Test
    void test_exception() {

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any()))
                .thenReturn(new ResponseEntity<>("{\"price\": 150.0}", HttpStatus.OK));

        Assertions.assertThrows(StockValuationApiException.class, () -> technicalDataService.fetchTechnicalData("TEST"));

    }
}
