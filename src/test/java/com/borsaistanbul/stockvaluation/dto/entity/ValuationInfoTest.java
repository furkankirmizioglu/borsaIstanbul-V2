package com.borsaistanbul.stockvaluation.dto.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ValuationInfoTest {

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
    void test() {

        assertEquals(123L, valuationInfo.getId());
        assertEquals(20230818L, valuationInfo.getLastUpdated());
        assertEquals("TEST", valuationInfo.getTicker());
        assertEquals("2023/06", valuationInfo.getBalanceSheetTerm());
        assertEquals(10.0, valuationInfo.getEquity());
        assertEquals(10.0, valuationInfo.getInitialCapital());
        assertEquals(10.0, valuationInfo.getAnnualEbitda());
        assertEquals(10.0, valuationInfo.getAnnualNetProfit());
        assertEquals(10.0, valuationInfo.getNetDebt());
        
    }

}
