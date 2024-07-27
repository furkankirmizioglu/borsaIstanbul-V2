package com.borsaistanbul.stockvaluation.dto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FinancialValuesTest {

    private FinancialValues financialValues;

    @BeforeEach
    void init() {

        financialValues = new FinancialValues();
        financialValues.setCashAndEquivalents(BigDecimal.TEN);
        financialValues.setFinancialInvestments(BigDecimal.TEN);
        financialValues.setTotalFinancialLiabilities(new BigDecimal(100));
        financialValues.setAnnualGrossProfit(BigDecimal.TEN);
        financialValues.setAdministrativeExpenses(BigDecimal.TEN);
        financialValues.setMarketingSalesDistributionExpenses(BigDecimal.TEN);
        financialValues.setResearchDevelopmentExpenses(BigDecimal.TEN);
        financialValues.setAmortization(BigDecimal.TEN);
        financialValues.setEquities(BigDecimal.TEN);
        financialValues.setInitialCapital(BigDecimal.TEN);
        financialValues.setTotalAssets(BigDecimal.TEN);
        financialValues.setTotalShortTermLiabilities(BigDecimal.TEN);
        financialValues.setTotalLongTermLiabilities(BigDecimal.TEN);
        financialValues.setAnnualSales(BigDecimal.TEN);
        financialValues.setAnnualNetProfit(BigDecimal.TEN);
        financialValues.setPrevYearNetProfit(BigDecimal.TEN);
    }

    @Test
    void test() {
        assertEquals(BigDecimal.TEN, financialValues.getCashAndEquivalents());
        assertEquals(BigDecimal.TEN, financialValues.getFinancialInvestments());
        assertEquals(new BigDecimal(100), financialValues.getTotalFinancialLiabilities());
        assertEquals(BigDecimal.TEN, financialValues.getAnnualGrossProfit());
        assertEquals(BigDecimal.TEN, financialValues.getAdministrativeExpenses());
        assertEquals(BigDecimal.TEN, financialValues.getMarketingSalesDistributionExpenses());
        assertEquals(BigDecimal.TEN, financialValues.getResearchDevelopmentExpenses());
        assertEquals(BigDecimal.TEN, financialValues.getAmortization());
        assertEquals(BigDecimal.TEN, financialValues.getEquities());
        assertEquals(BigDecimal.TEN, financialValues.getInitialCapital());
        assertEquals(BigDecimal.TEN, financialValues.getTotalAssets());
        assertEquals(BigDecimal.TEN, financialValues.getTotalShortTermLiabilities());
        assertEquals(BigDecimal.TEN, financialValues.getTotalLongTermLiabilities());
        assertEquals(BigDecimal.TEN, financialValues.getAnnualSales());
        assertEquals(BigDecimal.TEN, financialValues.getAnnualNetProfit());
        assertEquals(BigDecimal.TEN, financialValues.getPrevYearNetProfit());
    }

}
