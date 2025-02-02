package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import lombok.experimental.UtilityClass;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.usermodel.Row;

@UtilityClass
public class CalculateTools {

    public static double getFirstCellValue(Row row) {
        return (row.getCell(1) != null) ? row.getCell(1).getNumericCellValue() : 0.00;
    }

    public static double priceToEarnings(double price, ValuationInfo info) {
        double currentEPS = info.getAnnualNetProfit() / info.getInitialCapital();
        double pe = Precision.round(price / currentEPS, 2);
        return pe > 0 ? pe : Double.NaN;
    }

    public static double priceToBookRatio(double price, ValuationInfo info) {
        return Precision.round(info.getInitialCapital() * price / info.getEquity(), 2);
    }

    public static double netDebtToEbitda(ValuationInfo valuationInfo) {
        return Precision.round(valuationInfo.getNetDebt() / valuationInfo.getAnnualEbitda(), 2);
    }

    public static double netCashPerShare(ValuationInfo info) {
        return Precision.round(info.getNetCash() / info.getInitialCapital(), 2);
    }

    public static double enterpriseValueToEbitda(double price, ValuationInfo valuationInfo) {
        double enterpriseValue = valuationInfo.getInitialCapital() * price + valuationInfo.getNetDebt();
        double evToEbitda = Precision.round(enterpriseValue / valuationInfo.getAnnualEbitda(), 2);
        return evToEbitda > 0 ? evToEbitda : Double.NaN;
    }

    public static double marketValueToNetWorkingCapital(double price, ValuationInfo info) {
        return Precision.round((price * info.getInitialCapital()) / info.getNetWorkingCapital(), 2);
    }

}