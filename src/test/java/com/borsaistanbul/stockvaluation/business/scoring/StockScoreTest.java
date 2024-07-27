package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class StockScoreTest {

    private StockScoreImpl stockScore;
    private List<ResponseData> responseDataList;
    private static final String TEST = "TEST";
    private static final double defaultDouble = 10.0;

    @BeforeEach
    void init() {
        stockScore = new StockScoreImpl();
        responseDataList = new ArrayList<>();

        ResponseData responseData1 = ResponseData.builder()
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

        ResponseData responseData2 = ResponseData.builder()
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

        ResponseData responseData3 = ResponseData.builder()
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

        responseDataList.add(responseData1);
        responseDataList.add(responseData2);
        responseDataList.add(responseData3);
    }

    @Test
    void test() {
        stockScore.scoring(responseDataList);
        Assertions.assertNotNull(responseDataList);
    }

}
