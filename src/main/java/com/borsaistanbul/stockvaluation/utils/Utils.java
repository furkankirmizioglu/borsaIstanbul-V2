package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import org.apache.commons.math3.util.Precision;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static long getCurrentDateTimeAsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        return Long.parseLong(dtf.format(now));
    }

    public static double priceToEarningsGrowth(double closePrice, ValuationInfo info) {
        double EPS_2 = Precision.round(info.getTtmNetProfit().doubleValue() / info.getInitialCapital().doubleValue(), 2);
        double EPS_1 = Precision.round(info.getPrevTtmNetProfit().doubleValue() / info.getPrevInitialCapital().doubleValue(), 2);
        double EPS_GROWTH_RATE = Precision.round((EPS_2 / EPS_1 - 1) * 100, 2);
        double PE = Precision.round(closePrice / EPS_2, 2);
        double priceToEarningsGrowth = Precision.round(PE / EPS_GROWTH_RATE, 2);
        return (priceToEarningsGrowth) > 0 ? priceToEarningsGrowth : 0;
    }

    public static double priceToBookRatio(double closePrice, ValuationInfo info) {
        double marketValue = info.getInitialCapital().doubleValue() * closePrice;
        return Precision.round(marketValue / info.getMainEquity().doubleValue(), 2);
    }

    public static BigDecimal stringToBigDecimal(String value) {
        return (value != null) ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    public static double longTermDebtToEquity(ValuationInfo info) {
        return Precision.round(info.getLongTermLiabilities().doubleValue() / info.getMainEquity().doubleValue() * 100, 2);
    }
}
