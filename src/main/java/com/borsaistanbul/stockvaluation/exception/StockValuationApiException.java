package com.borsaistanbul.stockvaluation.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;
@Getter
@Setter
public class StockValuationApiException extends RuntimeException {
    private final String responseCode;
    private final String responseDesc;
    private final Serializable data;

    @Builder
    public StockValuationApiException(String responseCode, String responseDesc, @Nullable Serializable data) {
        this.responseCode = responseCode;
        this.responseDesc = responseDesc;
        this.data = data;
    }

}
