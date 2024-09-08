package com.borsaistanbul.stockvaluation.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class StockValuationApiException extends RuntimeException {
    private final String responseCode;
    private final String responseDesc;

    @Builder
    public StockValuationApiException(String responseCode, String responseDesc) {
        this.responseCode = responseCode;
        this.responseDesc = responseDesc;
    }

}
