package com.borsaistanbul.stockvaluation.common;

import lombok.Data;

@Data
public class StockValuationApiResponse<T> {

    private String responseCode;
    private String responseDesc;
    private T data;

}
