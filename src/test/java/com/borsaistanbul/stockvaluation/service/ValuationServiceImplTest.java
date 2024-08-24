package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.business.scoring.StockScore;
import com.borsaistanbul.stockvaluation.business.valuation.ValuationBusiness;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValuationServiceImplTest {

    @Mock
    private StockScore stockScore;

    @Mock
    private ValuationBusiness valuationBusiness;

    private ValuationService valuationService;
    private static final String TEST = "TEST";
    private static final double defaultDouble = 10.0;
    private List<ResponseData> responseDataList;

    @BeforeEach
    void init() {
        responseDataList = List.of(ResponseData.builder()
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
                .build());

        valuationService = new ValuationServiceImpl(valuationBusiness, stockScore);
    }

    @Test
    void test() {
        when(stockScore.scoring(anyList())).thenReturn(responseDataList);
        List<ResponseData> output = valuationService.valuation("Bankacılık");
        assertFalse(output.isEmpty());
    }
}
