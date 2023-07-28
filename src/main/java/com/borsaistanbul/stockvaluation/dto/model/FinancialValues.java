package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FinancialValues {

    private BigDecimal cashAndEquivalents = BigDecimal.ZERO;
    private BigDecimal financialInvestments = BigDecimal.ZERO;
    private BigDecimal totalFinancialLiabilities = BigDecimal.ZERO;
    private BigDecimal grossProfit = BigDecimal.ZERO;
    private BigDecimal administrativeExpenses = BigDecimal.ZERO;
    private BigDecimal marketingSalesDistributionExpenses = BigDecimal.ZERO;
    private BigDecimal researchDevelopmentExpenses = BigDecimal.ZERO;
    private BigDecimal depreciationAmortization = BigDecimal.ZERO;

    private BigDecimal equities = BigDecimal.ZERO;
    private BigDecimal initialCapital = BigDecimal.ZERO;

    private BigDecimal annualSales = BigDecimal.ZERO;
    private BigDecimal ttmNetProfit = BigDecimal.ZERO;
    private BigDecimal prevTtmNetProfit = BigDecimal.ZERO;
}
