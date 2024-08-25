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

    public static double debtToEquityRatio(ValuationInfo info) {
        return Precision.round(info.getTotalDebt().doubleValue() / info.getEquity().doubleValue() * 100, 2);
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

        return Precision.round(enterpriseValue.doubleValue() / valuationInfo.getAnnualEbitda().doubleValue(), 2);
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
        return values.getShortTermFinancialDebts()
                .add(values.getLongTermFinancialDebts())
                .subtract(values.getCashAndEquivalents())
                .subtract(values.getFinancialInvestments());

    }
}