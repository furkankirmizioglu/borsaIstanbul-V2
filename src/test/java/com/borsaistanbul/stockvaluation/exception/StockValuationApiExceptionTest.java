package com.borsaistanbul.stockvaluation.exception;

import com.borsaistanbul.stockvaluation.utils.ResponseCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockValuationApiExceptionTest {

    private StockValuationApiException exception;

    @BeforeEach
    void init() {
        exception = new StockValuationApiException(ResponseCodes.OK, ResponseCodes.OK_MESSAGE, "OK");
        exception = StockValuationApiException.builder()
                .responseCode(ResponseCodes.OK)
                .responseDesc(ResponseCodes.OK_MESSAGE)
                .data("OK")
                .build();
    }

    @Test
    void test() {
        assertEquals(ResponseCodes.OK, exception.getResponseCode());
        assertEquals(ResponseCodes.OK_MESSAGE, exception.getResponseDesc());
        assertEquals("OK", exception.getData());
    }

}
