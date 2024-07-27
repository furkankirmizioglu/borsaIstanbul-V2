package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.FinancialValues;
import lombok.experimental.UtilityClass;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

@UtilityClass
public class CalculateTools {

    public static double priceToEarnings(double price, ValuationInfo info) {
        double currentEPS = info.getAnnualNetProfit().doubleValue() / info.getInitialCapital().doubleValue();
        double pe = Precision.round(price / currentEPS, 2);
        return pe > 0 ? pe : Double.NaN;
    }

    public static double priceToBookRatio(double price, ValuationInfo info) {
        return Precision.round(info.getInitialCapital().doubleValue() * price / info.getEquity().doubleValue(), 2);
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

    public static double enterpriseValueToEbitda(double price, ValuationInfo valuationInfo) {
        BigDecimal enterpriseValue = valuationInfo.getInitialCapital()
                .multiply(BigDecimal.valueOf(price))
                .add(valuationInfo.getNetDebt());

        return Precision.round(enterpriseValue.doubleValue() / valuationInfo.getNetDebt().doubleValue(), 2);
    }

    public static double debtToEquity(ValuationInfo valuationInfo) {
        return Precision.round(valuationInfo.getNetDebt().doubleValue() / valuationInfo.getEquity().doubleValue(), 2);
    }

    // =============== PARSING BALANCE SHEET UTILITY FUNCTIONS ==================

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