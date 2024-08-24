package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.FinancialValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class CalculateToolsTest {

    private ValuationInfo valuationInfo;
    private FinancialValues financialValues;

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

        financialValues = new FinancialValues();
        financialValues.setAdministrativeExpenses(BigDecimal.TEN);
        financialValues.setAmortization(BigDecimal.TEN);
        financialValues.setMarketingSalesDistributionExpenses(BigDecimal.TEN);
        financialValues.setResearchDevelopmentExpenses(BigDecimal.TEN);
        financialValues.setCashAndEquivalents(BigDecimal.TEN);
        financialValues.setFinancialInvestments(BigDecimal.TEN);
        financialValues.setAnnualGrossProfit(BigDecimal.TEN);
    }

    @Test
    void priceToEarningsTest() {
        double pe = CalculateTools.priceToEarnings(10.0, valuationInfo);
        Assertions.assertEquals(10, pe);
    }

    @Test
    void leverageRatioTest() {
        double result = CalculateTools.leverageRatio(valuationInfo);
        Assertions.assertEquals(200, result);
    }

    @Test
    void netDebt() {
        BigDecimal result = CalculateTools.netDebt(financialValues);
        Assertions.assertEquals(new BigDecimal(80), result);
    }
    @Test
    void ebitda() {
        BigDecimal result = CalculateTools.ebitda(financialValues);
        Assertions.assertEquals(new BigDecimal(50), result);
    }

    @Test
    void netDebtToEbitda() {
        double result = CalculateTools.netDebtToEbitda(valuationInfo);
        Assertions.assertEquals(1, result);
    }
}