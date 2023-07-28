package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.common.AbstractBaseApi;
import com.borsaistanbul.stockvaluation.common.StockValuationApiResponse;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.service.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/valuation")
public class ValuationApiImpl extends AbstractBaseApi<String, ValuationApiResponse> implements ValuationApi {

    private final ValuationService service;

    @Autowired
    public ValuationApiImpl(ValuationService service) {
        this.service = service;
    }

    @Override
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<StockValuationApiResponse<ValuationApiResponse>> valuation(String request) {
        return perform(request);
    }

    @Override
    public ValuationApiResponse doOperation(String input) {
        List<ResponseData> data = service.valuation(input);
        return ValuationApiResponse.builder()
                .responseDataList(data)
                .build();
    }
}