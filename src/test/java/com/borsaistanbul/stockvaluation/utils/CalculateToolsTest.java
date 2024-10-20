package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateToolsTest {

    private ValuationInfo valuationInfo;

    @BeforeEach
    void init() {
        valuationInfo = new ValuationInfo();
        valuationInfo.setId(123L);
        valuationInfo.setLastUpdated(20230818L);
        valuationInfo.setTicker("TEST");
        valuationInfo.setBalanceSheetTerm("2023/06");
        valuationInfo.setEquity(10.0);
        valuationInfo.setInitialCapital(10.0);
        valuationInfo.setAnnualEbitda(10.0);
        valuationInfo.setAnnualNetProfit(10.0);
        valuationInfo.setNetDebt(10.0);
    }

    @Test
    void priceToEarningsTest() {
        double pe = CalculateTools.priceToEarnings(10.0, valuationInfo);
        Assertions.assertEquals(10, pe);
    }

    @Test
    void netDebtToEbitda() {
        double result = CalculateTools.netDebtToEbitda(valuationInfo);
        Assertions.assertEquals(1, result);
    }
}