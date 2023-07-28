package com.borsaistanbul.stockvaluation.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseCodes {

    public static final String OK = "1";
    public static final String OK_MESSAGE = "Success";
    public static final String API_EXCEPTION = "3";
    public static final String API_EXCEPTION_MESSAGE = "StockValuationApiException: ";
    public static final String UNKNOWN_ERROR = "99";
    public static final String UNKNOWN_ERROR_MESSAGE = "Unexpected exception occurred: ";

}
