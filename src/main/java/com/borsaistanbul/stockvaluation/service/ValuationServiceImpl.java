package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.business.scoring.StockScore;
import com.borsaistanbul.stockvaluation.business.valuation.ValuationBusiness;
import com.borsaistanbul.stockvaluation.dto.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValuationServiceImpl implements ValuationService {

    @Autowired
    private final ValuationBusiness valuationBusiness;
    @Autowired
    private final StockScore stockScore;

    public ValuationServiceImpl(ValuationBusiness valuationBusiness, StockScore stockScore) {
        this.valuationBusiness = valuationBusiness;
        this.stockScore = stockScore;
    }

    @Override
    public List<Response> valuation(String industry) {

        List<Response> responseList = valuationBusiness.business(industry);

        // Sort the valuationResultList by finalScore and send response to UI.
        stockScore.scoring(responseList);
        return responseList;
    }
}
