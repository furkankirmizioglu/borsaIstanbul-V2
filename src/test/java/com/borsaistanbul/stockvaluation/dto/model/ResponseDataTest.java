package com.borsaistanbul.stockvaluation.dto.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                .debtToEquity(defaultDouble)
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
        responseData.setDebtToEquity(defaultDouble);
        responseData.setFinalScore(defaultDouble);
        responseData.setSuggestion(TEST);
    }

    @Test
    void test() {
        Assertions.assertEquals(TEST, responseData.getTicker());
        Assertions.assertEquals(TEST, responseData.getCompanyName());
        Assertions.assertEquals(TEST, responseData.getLatestBalanceSheetTerm());
        Assertions.assertEquals(defaultDouble, responseData.getPrice());
        Assertions.assertEquals(defaultDouble, responseData.getPe());
        Assertions.assertEquals(defaultDouble, responseData.getPb());
        Assertions.assertEquals(defaultDouble, responseData.getEnterpriseValueToEbitda());
        Assertions.assertEquals(defaultDouble, responseData.getDebtToEquity());
        Assertions.assertEquals(defaultDouble, responseData.getNetDebtToEbitda());
        Assertions.assertEquals(TEST, responseData.getSuggestion());
    }

}
