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
    private static final double defaultDouble = 10.0;

    @BeforeEach
    void init() {

        responseData = ResponseData.builder()
                .ticker(TEST)
                .companyName(TEST)
                .latestBalanceSheetTerm(TEST)
                .price(defaultDouble)
                .pe(defaultDouble)
                .pb(defaultDouble)
                .enterpriseValueToEbitda(defaultDouble)
                .netDebtToEbitda(defaultDouble)
                .leverage(defaultDouble)
                .finalScore(defaultDouble)
                .suggestion(TEST)
                .build();

        responseData.setTicker(TEST);
        responseData.setCompanyName(TEST);
        responseData.setLatestBalanceSheetTerm(TEST);
        responseData.setPrice(defaultDouble);
        responseData.setPe(defaultDouble);
        responseData.setPb(defaultDouble);
        responseData.setEnterpriseValueToEbitda(defaultDouble);
        responseData.setNetDebtToEbitda(defaultDouble);
        responseData.setLeverage(defaultDouble);
        responseData.setFinalScore(defaultDouble);
        responseData.setSuggestion(TEST);
    }

    @Test
    void test() {
        assertEquals(TEST, responseData.getTicker());
        assertEquals(TEST, responseData.getCompanyName());
        assertEquals(TEST, responseData.getLatestBalanceSheetTerm());
        assertEquals(defaultDouble, responseData.getPrice());
        assertEquals(defaultDouble, responseData.getPe());
        assertEquals(defaultDouble, responseData.getPb());
        assertEquals(defaultDouble, responseData.getEnterpriseValueToEbitda());
        assertEquals(defaultDouble, responseData.getLeverage());
        assertEquals(defaultDouble, responseData.getNetDebtToEbitda());
        assertEquals(TEST, responseData.getSuggestion());
    }

}
