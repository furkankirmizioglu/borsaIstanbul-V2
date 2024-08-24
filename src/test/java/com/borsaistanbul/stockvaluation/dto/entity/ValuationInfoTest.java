package com.borsaistanbul.stockvaluation.dto.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
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
        valuationInfo.setAnnualNetProfit(BigDecimal.TEN);
        valuationInfo.setPrevYearNetProfit(new BigDecimal(5));
        valuationInfo.setNetDebt(BigDecimal.TEN);
        valuationInfo.setTotalAssets(BigDecimal.TEN);
        valuationInfo.setLongTermLiabilities(BigDecimal.TEN);
        valuationInfo.setShortTermLiabilities(BigDecimal.TEN);
    }

    @Test
    void test() {

        assertEquals(123L, valuationInfo.getGuid());
        assertEquals(20230818L, valuationInfo.getLastUpdated());
        assertEquals("TEST", valuationInfo.getTicker());
        assertEquals("2023/06", valuationInfo.getBalanceSheetTerm());
        assertEquals(BigDecimal.TEN, valuationInfo.getEquity());
        assertEquals(BigDecimal.TEN, valuationInfo.getInitialCapital());
        assertEquals(BigDecimal.TEN, valuationInfo.getAnnualEbitda());
        assertEquals(BigDecimal.TEN, valuationInfo.getAnnualNetProfit());
        assertEquals(new BigDecimal(5), valuationInfo.getPrevYearNetProfit());
        assertEquals(BigDecimal.TEN, valuationInfo.getNetDebt());
        assertEquals(BigDecimal.TEN, valuationInfo.getTotalAssets());
        assertEquals(BigDecimal.TEN, valuationInfo.getLongTermLiabilities());
        assertEquals(BigDecimal.TEN, valuationInfo.getShortTermLiabilities());
        
    }

}
