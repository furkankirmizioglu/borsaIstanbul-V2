package com.borsaistanbul.stockvaluation.dto.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ValuationInfoTest {

    private ValuationInfo valuationInfo;

    @BeforeEach
    void init() {
        valuationInfo = new ValuationInfo();
        valuationInfo.setGuid(123L);
        valuationInfo.setLastUpdated(20230818L);
        valuationInfo.setTicker("TEST");
        valuationInfo.setBalanceSheetTerm("2023/06");
        valuationInfo.setEquity(BigDecimal.TEN);
        valuationInfo.setInitialCapital(BigDecimal.TEN);
        valuationInfo.setAnnualEbitda(BigDecimal.TEN);
        valuationInfo.setAnnualSales(BigDecimal.TEN);
        valuationInfo.setAnnualNetProfit(BigDecimal.TEN);
        valuationInfo.setPrevYearNetProfit(new BigDecimal(5));
        valuationInfo.setNetDebt(BigDecimal.TEN);
        valuationInfo.setTotalAssets(BigDecimal.TEN);
        valuationInfo.setLongTermLiabilities(BigDecimal.TEN);
        valuationInfo.setShortTermLiabilities(BigDecimal.TEN);
    }

    @Test
    void test() {

        Assertions.assertEquals(123L, valuationInfo.getGuid());
        Assertions.assertEquals(20230818L, valuationInfo.getLastUpdated());
        Assertions.assertEquals("TEST", valuationInfo.getTicker());
        Assertions.assertEquals("2023/06", valuationInfo.getBalanceSheetTerm());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getEquity());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getInitialCapital());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getAnnualEbitda());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getAnnualSales());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getAnnualNetProfit());
        Assertions.assertEquals(new BigDecimal(5), valuationInfo.getPrevYearNetProfit());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getNetDebt());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getTotalAssets());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getLongTermLiabilities());
        Assertions.assertEquals(BigDecimal.TEN, valuationInfo.getShortTermLiabilities());
        
    }

}
