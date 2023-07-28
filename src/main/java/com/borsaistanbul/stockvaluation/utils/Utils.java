package com.borsaistanbul.stockvaluation.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public final class Utils {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }

    public static long getCurrentDateTimeAsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        return Long.parseLong(dtf.format(now));
    }

}
