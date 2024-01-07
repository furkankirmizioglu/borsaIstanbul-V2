package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
public class FinancialValues {

    private BigDecimal cashAndEquivalents;
    private BigDecimal financialInvestments;
    private BigDecimal totalFinancialLiabilities;
    private BigDecimal grossProfit;
    private BigDecimal administrativeExpenses;
    private BigDecimal marketingSalesDistributionExpenses;
    private BigDecimal researchDevelopmentExpenses;
    private BigDecimal amortization;

    private BigDecimal equities;
    private BigDecimal initialCapital;
    private BigDecimal totalAssets;
    private BigDecimal totalShortTermLiabilities;
    private BigDecimal totalLongTermLiabilities;

    private BigDecimal annualSales;
    private BigDecimal incomeFromOtherFields;
    private BigDecimal ttmNetProfit;
    private BigDecimal prevTtmNetProfit;

    public void nullToZeroConverter() {

        this.cashAndEquivalents = setIfNullToZero(this.cashAndEquivalents);
        this.financialInvestments = setIfNullToZero(this.financialInvestments);
        this.totalFinancialLiabilities = setIfNullToZero(this.totalFinancialLiabilities);
        this.grossProfit = setIfNullToZero(this.grossProfit);
        this.administrativeExpenses = setIfNullToZero(this.administrativeExpenses);
        this.marketingSalesDistributionExpenses = setIfNullToZero(this.marketingSalesDistributionExpenses);
        this.researchDevelopmentExpenses = setIfNullToZero(this.researchDevelopmentExpenses);
        this.amortization = setIfNullToZero(this.amortization);
        this.equities = setIfNullToZero(this.equities);
        this.initialCapital = setIfNullToZero(this.initialCapital);
        this.totalAssets = setIfNullToZero(this.totalAssets);
        this.totalShortTermLiabilities = setIfNullToZero(this.totalShortTermLiabilities);
        this.totalLongTermLiabilities = setIfNullToZero(this.totalLongTermLiabilities);
        this.annualSales = setIfNullToZero(this.annualSales);
        this.incomeFromOtherFields = setIfNullToZero(this.incomeFromOtherFields);
        this.ttmNetProfit = setIfNullToZero(this.ttmNetProfit);
        this.prevTtmNetProfit = setIfNullToZero(this.prevTtmNetProfit);
    }


    private BigDecimal setIfNullToZero(BigDecimal field) {
        return Objects.requireNonNullElse(field, BigDecimal.ZERO);
    }
}
