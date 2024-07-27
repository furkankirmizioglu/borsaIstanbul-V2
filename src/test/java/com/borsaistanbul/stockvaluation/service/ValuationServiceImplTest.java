package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.business.scoring.StockScore;
import com.borsaistanbul.stockvaluation.business.valuation.ValuationBusiness;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class ValuationServiceImplTest {

    private ValuationService valuationService;
    private StockScore stockScore;
    private static final String TEST = "TEST";
    private static final double defaultDouble = 10.0;
    private List<ResponseData> responseDataList;

    @BeforeEach
    void init() {
        ValuationBusiness valuationBusiness = mock(ValuationBusiness.class);
        stockScore = mock(StockScore.class);
        responseDataList = new ArrayList<>();

        responseDataList.add(ResponseData.builder()
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
                .build());

        valuationService = new ValuationServiceImpl(valuationBusiness, stockScore);
    }

    @Test
    void test() {
        when(valuationService.valuation(anyString())).thenReturn(responseDataList);
        when(stockScore.scoring(anyList())).thenReturn(responseDataList);
        List<ResponseData> output = valuationService.valuation("Bankacılık");
        Assertions.assertFalse(output.isEmpty());
    }
}
