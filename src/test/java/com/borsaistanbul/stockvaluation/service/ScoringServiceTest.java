package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    private ScoringService stockScore;
    private List<ResponseData> responseDataList;
    private static final String TEST = "TEST";
    private static final double DEFAULT_DOUBLE = 10.0;

    @BeforeEach
    void init() {
        stockScore = new ScoringServiceImpl();
        responseDataList = new ArrayList<>();

        ResponseData responseData1 = ResponseData.builder()
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

        ResponseData responseData2 = ResponseData.builder()
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

        ResponseData responseData3 = ResponseData.builder()
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

        responseDataList.add(responseData1);
        responseDataList.add(responseData2);
        responseDataList.add(responseData3);
    }

    @Test
    void test() {
        stockScore.sortAndScore(responseDataList);
        assertNotNull(responseDataList);
    }

}
