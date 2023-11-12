package com.borsaistanbul.stockvaluation.dto.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class FinancialValuesTest {

    private FinancialValues financialValues;

    @BeforeEach
    void init() {

        financialValues = new FinancialValues();
        financialValues.setCashAndEquivalents(BigDecimal.TEN);
        financialValues.setFinancialInvestments(BigDecimal.TEN);
        financialValues.setTotalFinancialLiabilities(new BigDecimal(100));
        financialValues.setGrossProfit(BigDecimal.TEN);
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
        financialValues.setTtmNetProfit(BigDecimal.TEN);
        financialValues.setPrevTtmNetProfit(BigDecimal.TEN);
    }

    @Test
    void test() {
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getCashAndEquivalents());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getFinancialInvestments());
        Assertions.assertEquals(new BigDecimal(100), financialValues.getTotalFinancialLiabilities());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getGrossProfit());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getAdministrativeExpenses());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getMarketingSalesDistributionExpenses());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getResearchDevelopmentExpenses());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getAmortization());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getEquities());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getInitialCapital());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getTotalAssets());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getTotalShortTermLiabilities());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getTotalLongTermLiabilities());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getAnnualSales());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getTtmNetProfit());
        Assertions.assertEquals(BigDecimal.TEN, financialValues.getPrevTtmNetProfit());
    }

}
