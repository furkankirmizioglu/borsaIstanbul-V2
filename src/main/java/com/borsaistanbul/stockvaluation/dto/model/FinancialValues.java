package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
public class FinancialValues {

    // NET DEBT FIELDS.
    private BigDecimal cashAndEquivalents;
    private BigDecimal financialInvestments;
    private BigDecimal shortTermFinancialDebts;
    private BigDecimal longTermFinancialDebts;

    // EBITDA FIELDS.
    private BigDecimal annualGrossProfit;
    private BigDecimal administrativeExpenses;
    private BigDecimal marketingSalesDistributionExpenses;
    private BigDecimal researchDevelopmentExpenses;
    private BigDecimal amortization;

    // NOPAT FIELDS.
    private BigDecimal operationalProfitBeforeTax;
    private BigDecimal operationalTax;

    private BigDecimal equities;
    private BigDecimal initialCapital;
    private BigDecimal annualNetProfit;


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
        this.annualNetProfit = setIfNullToZero(this.annualNetProfit);
        this.operationalProfitBeforeTax = setIfNullToZero(this.operationalProfitBeforeTax);
        this.operationalTax = setIfNullToZero(this.operationalTax);
    }


    private BigDecimal setIfNullToZero(BigDecimal field) {
        return Objects.requireNonNullElse(field, BigDecimal.ZERO);
    }
}
