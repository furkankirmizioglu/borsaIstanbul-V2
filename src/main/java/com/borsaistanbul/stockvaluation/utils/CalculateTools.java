package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.FinancialValues;
import lombok.experimental.UtilityClass;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

@UtilityClass
public class CalculateTools {

    public static double priceToEarnings(double closePrice, ValuationInfo info) {
        double currentEPS = info.getAnnualNetProfit().doubleValue() / info.getInitialCapital().doubleValue();
        double pe = Precision.round(closePrice / currentEPS, 2);
        return pe > 0 ? pe : Double.NaN;
    }

    public static double priceToEarningsGrowth(double closePrice, ValuationInfo info) {
        double epsGrowthRate = (info.getAnnualNetProfit().doubleValue() - info.getPrevYearNetProfit().doubleValue()) / info.getPrevYearNetProfit().doubleValue() * 100;
        double pe = priceToEarnings(closePrice, info);
        return (pe > 0 && epsGrowthRate > 0) ? Precision.round(pe / epsGrowthRate, 4) : Double.NaN;
    }

    public static double priceToBookRatio(double closePrice, ValuationInfo info) {
        return Precision.round(info.getInitialCapital().doubleValue() * closePrice / info.getEquity().doubleValue(), 2);
    }

    public static double netProfitMargin(ValuationInfo info) {
        double netProfitMargin = Precision.round(info.getAnnualNetProfit().doubleValue() / info.getAnnualSales().doubleValue() * 100, 2);
        return (netProfitMargin != Double.NEGATIVE_INFINITY && netProfitMargin != Double.POSITIVE_INFINITY) ? netProfitMargin : Double.NaN;
    }

    public static double ebitdaMargin(ValuationInfo info) {
        double ebitdaMargin = Precision.round(info.getAnnualEbitda().doubleValue() / (info.getAnnualSales()).doubleValue() * 100, 2);
        return (ebitdaMargin != Double.NEGATIVE_INFINITY && ebitdaMargin != Double.POSITIVE_INFINITY) ? ebitdaMargin : Double.NaN;
    }

    public static double leverageRatio(ValuationInfo info) {
        BigDecimal totalLiabilities = info.getLongTermLiabilities().add(info.getShortTermLiabilities());
        return Precision.round(totalLiabilities.doubleValue() / info.getTotalAssets().doubleValue() * 100, 2);
    }

    public static BigDecimal cellValue(Row row, int i) {
        return (row.getCell(i) != null) ? BigDecimal.valueOf(row.getCell(i).getNumericCellValue()) : BigDecimal.ZERO;
    }

    public static double netDebtToEbitda(ValuationInfo valuationInfo) {
        return Precision.round(valuationInfo.getNetDebt().doubleValue() / valuationInfo.getAnnualEbitda().doubleValue(), 2);
    }

    public static double marketValueToEbitda(double closePrice, ValuationInfo valuationInfo) {

        double marketValue = valuationInfo.getInitialCapital().doubleValue() * closePrice;
        double actualOperationProfit = valuationInfo.getAnnualEbitda().doubleValue();
        return Precision.round(marketValue / actualOperationProfit, 2);

    }

    public static BigDecimal ebitda(FinancialValues values) {
        return values.getAnnualGrossProfit()
                .add(values.getAdministrativeExpenses())
                .add(values.getMarketingSalesDistributionExpenses())
                .add(values.getResearchDevelopmentExpenses())
                .add(values.getAmortization());
    }

    public static BigDecimal netDebt(FinancialValues values) {
        return values.getTotalFinancialLiabilities()
                .subtract(values.getCashAndEquivalents())
                .subtract(values.getFinancialInvestments());
    }
}