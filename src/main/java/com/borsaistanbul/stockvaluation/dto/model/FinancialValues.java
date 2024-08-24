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
    private BigDecimal shortTermFinancialDebts;
    private BigDecimal longTermFinancialDebts;
    private BigDecimal annualGrossProfit;
    private BigDecimal administrativeExpenses;
    private BigDecimal marketingSalesDistributionExpenses;
    private BigDecimal researchDevelopmentExpenses;
    private BigDecimal amortization;

    private BigDecimal equities;
    private BigDecimal initialCapital;
    private BigDecimal totalAssets;
    private BigDecimal totalShortTermLiabilities;
    private BigDecimal totalLongTermLiabilities;

    private BigDecimal incomeFromOtherFields;
    private BigDecimal annualNetProfit;
    private BigDecimal prevYearNetProfit;

    public void nullToZeroConverter() {

        this.cashAndEquivalents = setIfNullToZero(this.cashAndEquivalents);
        this.financialInvestments = setIfNullToZero(this.financialInvestments);
        this.shortTermFinancialDebts = setIfNullToZero(this.shortTermFinancialDebts);
        this.longTermFinancialDebts = setIfNullToZero(this.longTermFinancialDebts);
        this.annualGrossProfit = setIfNullToZero(this.annualGrossProfit);
        this.administrativeExpenses = setIfNullToZero(this.administrativeExpenses);
        this.marketingSalesDistributionExpenses = setIfNullToZero(this.marketingSalesDistributionExpenses);
        this.researchDevelopmentExpenses = setIfNullToZero(this.researchDevelopmentExpenses);
        this.amortization = setIfNullToZero(this.amortization);
        this.equities = setIfNullToZero(this.equities);
        this.initialCapital = setIfNullToZero(this.initialCapital);
        this.totalAssets = setIfNullToZero(this.totalAssets);
        this.totalShortTermLiabilities = setIfNullToZero(this.totalShortTermLiabilities);
        this.totalLongTermLiabilities = setIfNullToZero(this.totalLongTermLiabilities);
        this.incomeFromOtherFields = setIfNullToZero(this.incomeFromOtherFields);
        this.annualNetProfit = setIfNullToZero(this.annualNetProfit);
        this.prevYearNetProfit = setIfNullToZero(this.prevYearNetProfit);
    }


    private BigDecimal setIfNullToZero(BigDecimal field) {
        return Objects.requireNonNullElse(field, BigDecimal.ZERO);
    }
}
