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
        String ticker = TEST;
        String companyName = TEST;
        String latestBalanceSheetTerm = TEST;
        double price = defaultDouble;
        double pe = defaultDouble;
        double pb = defaultDouble;
        double peg = defaultDouble;
        double ebitdaMargin = defaultDouble;
        double netProfitMargin = defaultDouble;
        double netDebtToEbitda = defaultDouble;
        double leverageRatio = defaultDouble;
        double finalScore = defaultDouble;
        String suggestion = TEST;

        responseData = ResponseData.builder()
                .ticker(ticker)
                .companyName(companyName)
                .latestBalanceSheetTerm(latestBalanceSheetTerm)
                .price(price)
                .pe(pe)
                .pb(pb)
                .peg(peg)
                .ebitdaMargin(ebitdaMargin)
                .netProfitMargin(netProfitMargin)
                .netDebtToEbitda(netDebtToEbitda)
                .leverageRatio(leverageRatio)
                .finalScore(finalScore)
                .suggestion(suggestion)
                .build();

        responseData.setTicker(ticker);
        responseData.setCompanyName(companyName);
        responseData.setLatestBalanceSheetTerm(latestBalanceSheetTerm);
        responseData.setPrice(price);
        responseData.setPe(pe);
        responseData.setPb(pb);
        responseData.setPeg(peg);
        responseData.setEbitdaMargin(ebitdaMargin);
        responseData.setNetProfitMargin(netProfitMargin);
        responseData.setNetDebtToEbitda(netDebtToEbitda);
        responseData.setLeverageRatio(leverageRatio);
        responseData.setFinalScore(finalScore);
        responseData.setSuggestion(suggestion);
    }

    @Test
    void test() {
        Assertions.assertEquals(TEST, responseData.getTicker());
        Assertions.assertEquals(TEST, responseData.getCompanyName());
        Assertions.assertEquals(TEST, responseData.getLatestBalanceSheetTerm());
        Assertions.assertEquals(defaultDouble, responseData.getPrice());
        Assertions.assertEquals(defaultDouble, responseData.getPe());
        Assertions.assertEquals(defaultDouble, responseData.getPb());
        Assertions.assertEquals(defaultDouble, responseData.getPeg());
        Assertions.assertEquals(defaultDouble, responseData.getEbitdaMargin());
        Assertions.assertEquals(defaultDouble, responseData.getNetProfitMargin());
        Assertions.assertEquals(defaultDouble, responseData.getNetDebtToEbitda());
        Assertions.assertEquals(defaultDouble, responseData.getLeverageRatio());
        Assertions.assertEquals(TEST, responseData.getSuggestion());
    }

}
