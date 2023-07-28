package com.borsaistanbul.stockvaluation.common;

import com.borsaistanbul.stockvaluation.exception.StockValuationApiException;
import com.borsaistanbul.stockvaluation.utils.ResponseCodes;
import com.borsaistanbul.stockvaluation.utils.Utils;
import com.borsaistanbul.stockvaluation.utils.RequestScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class AbstractBaseApi<I, O> {

    public ResponseEntity<StockValuationApiResponse<O>> perform(I request) {

        StockValuationApiResponse<O> stockValuationApiResponse = new StockValuationApiResponse<>();

        try {
            final O outputData = doOperation(request);
            stockValuationApiResponse.setResponseCode(ResponseCodes.OK);
            stockValuationApiResponse.setResponseDesc(ResponseCodes.OK_MESSAGE);
            stockValuationApiResponse.setData(outputData);
            return ResponseEntity.ok(stockValuationApiResponse);
        } catch (StockValuationApiException e) {
            log.error(ResponseCodes.API_EXCEPTION_MESSAGE, e);
            stockValuationApiResponse.setResponseCode(ResponseCodes.API_EXCEPTION);
            stockValuationApiResponse.setResponseDesc(ResponseCodes.API_EXCEPTION_MESSAGE + e.getResponseDesc());
            if (e.getData() != null) {
                stockValuationApiResponse.setData((O) e.getData());
            }
            return ResponseEntity.ok(stockValuationApiResponse);
        } catch (Exception e) {
            log.error(ResponseCodes.UNKNOWN_ERROR_MESSAGE, e);
            String errorDetail = "";
            if (!Utils.isNullOrEmpty(e.getMessage())) errorDetail = e.getMessage();
            stockValuationApiResponse.setResponseCode(ResponseCodes.UNKNOWN_ERROR);
            stockValuationApiResponse.setResponseDesc(ResponseCodes.UNKNOWN_ERROR_MESSAGE + errorDetail);
            return ResponseEntity.ok(stockValuationApiResponse);
        } finally {
            RequestScope.setErrorCode(stockValuationApiResponse.getResponseCode());
            RequestScope.setErrorMessage(stockValuationApiResponse.getResponseDesc());
            RequestScope.unload();
        }
    }

    public abstract O doOperation(I input);
}
