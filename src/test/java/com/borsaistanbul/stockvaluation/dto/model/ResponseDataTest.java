package com.borsaistanbul.stockvaluation.dto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ResponseDataTest {

    private ResponseData responseData;
    private static final String TEST = "TEST";
    private static final double DEFAULT_DOUBLE = 10.0;

    @BeforeEach
    void init() {

        responseData = ResponseData.builder()
                .ticker(TEST)
                .companyName(TEST)
                .latestBalanceSheetTerm(TEST)
                .price(DEFAULT_DOUBLE)
                .pe(DEFAULT_DOUBLE)
                .pb(DEFAULT_DOUBLE)
                .evToEbitda(DEFAULT_DOUBLE)
                .netDebtToEbitda(DEFAULT_DOUBLE)
                .finalScore(DEFAULT_DOUBLE)
                .suggestion(TEST)
                .build();

        responseData.setTicker(TEST);
        responseData.setCompanyName(TEST);
        responseData.setLatestBalanceSheetTerm(TEST);
        responseData.setPrice(DEFAULT_DOUBLE);
        responseData.setPe(DEFAULT_DOUBLE);
        responseData.setPb(DEFAULT_DOUBLE);
        responseData.setEvToEbitda(DEFAULT_DOUBLE);
        responseData.setNetDebtToEbitda(DEFAULT_DOUBLE);
        responseData.setFinalScore(DEFAULT_DOUBLE);
        responseData.setSuggestion(TEST);
    }

    @Test
    void test() {
        assertEquals(TEST, responseData.getTicker());
        assertEquals(TEST, responseData.getCompanyName());
        assertEquals(TEST, responseData.getLatestBalanceSheetTerm());
        assertEquals(DEFAULT_DOUBLE, responseData.getPrice());
        assertEquals(DEFAULT_DOUBLE, responseData.getPe());
        assertEquals(DEFAULT_DOUBLE, responseData.getPb());
        assertEquals(DEFAULT_DOUBLE, responseData.getEvToEbitda());
        assertEquals(DEFAULT_DOUBLE, responseData.getNetDebtToEbitda());
        assertEquals(TEST, responseData.getSuggestion());
    }

}
