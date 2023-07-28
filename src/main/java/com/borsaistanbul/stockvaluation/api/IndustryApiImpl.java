package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.common.AbstractBaseApi;
import com.borsaistanbul.stockvaluation.common.StockValuationApiResponse;
import com.borsaistanbul.stockvaluation.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/industry")
public class IndustryApiImpl extends AbstractBaseApi<IndustryApiRequest, IndustryApiResponse> implements IndustryApi {

    private final CompanyService service;

    @Autowired
    public IndustryApiImpl(CompanyService service) {
        this.service = service;
    }

    @Override
    public IndustryApiResponse doOperation(IndustryApiRequest input) {

        List<String> industryList = service.listAll();

        return IndustryApiResponse.builder()
                .industryList(industryList)
                .build();
    }

    @Override
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<StockValuationApiResponse<IndustryApiResponse>> listAll(IndustryApiRequest request) {
        return perform(request);
    }
}
