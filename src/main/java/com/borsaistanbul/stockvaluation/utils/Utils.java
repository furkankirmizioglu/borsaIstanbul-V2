package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Utils {
    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static long getCurrentDateTimeAsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        return Long.parseLong(dtf.format(now));
    }

    public static double priceToEarningsGrowth(double closePrice, ValuationInfo info) {
        double currentEPS = Precision.round(info.getTtmNetProfit().doubleValue() / info.getInitialCapital().doubleValue(), 2);
        double previousEPS = Precision.round(info.getPrevTtmNetProfit().doubleValue() / info.getInitialCapital().doubleValue(), 2);
        double epsGrowthRate = Precision.round((currentEPS / previousEPS - 1) * 100, 2);
        double pe = Precision.round(closePrice / currentEPS, 2);
        double priceToEarningsGrowth = Precision.round(pe / epsGrowthRate, 2);
        return (priceToEarningsGrowth) > 0 ? priceToEarningsGrowth : 0;
    }

    public static double priceToBookRatio(double closePrice, ValuationInfo info) {
        return Precision.round(info.getInitialCapital().doubleValue() * closePrice / info.getEquity().doubleValue(), 2);
    }

    public static double netProfitMargin(ValuationInfo info) {
        return Precision.round(info.getTtmNetProfit().doubleValue() / info.getAnnualSales().doubleValue() * 100, 2);
    }

    public static double ebitdaMargin(ValuationInfo info) {
        return Precision.round(info.getAnnualEbitda().doubleValue() / (info.getAnnualSales()).doubleValue() * 100, 2);
    }

    public static BigDecimal cellValue(Row row, int i) {
        return (row.getCell(i) != null) ? BigDecimal.valueOf(row.getCell(i).getNumericCellValue()) : BigDecimal.ZERO;
    }

    public static double netDebtToEbitda(ValuationInfo valuationInfo) {
        return Precision.round(valuationInfo.getNetDebt().doubleValue() / valuationInfo.getAnnualEbitda().doubleValue(), 2);
    }
}
