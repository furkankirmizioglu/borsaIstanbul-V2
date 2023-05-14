package com.borsaistanbul.stockvaluation.utils;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;

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
        double EPS_2 = info.getTtmNetProfit().doubleValue() / info.getInitialCapital().doubleValue();
        double EPS_1 = info.getPrevTtmNetProfit().doubleValue() / info.getPrevInitialCapital().doubleValue();
        double EPS_GROWTH_RATE = (EPS_2 / EPS_1 - 1) * 100;
        double PE = closePrice / EPS_2;
        double priceToEarningsGrowth = PE / EPS_GROWTH_RATE;
        return (priceToEarningsGrowth) > 0 ? priceToEarningsGrowth : 0;
    }

    public static double priceToBookRatio(double closePrice, ValuationInfo info) {
        double marketValue = info.getInitialCapital().doubleValue() * closePrice;
        return marketValue / info.getMainEquity().doubleValue();
    }

    public static BigDecimal stringToBigDecimal(String value) {
        return (value != null) ? new BigDecimal(value) : BigDecimal.ZERO;
    }

}
