package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.business.scoring.StockScore;
import com.borsaistanbul.stockvaluation.business.valuation.ValuationBusiness;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValuationServiceImpl implements ValuationService {

    private final ValuationBusiness valuationBusiness;
    private final StockScore stockScore;

    @Autowired
    public ValuationServiceImpl(ValuationBusiness valuationBusiness, StockScore stockScore) {
        this.valuationBusiness = valuationBusiness;
        this.stockScore = stockScore;
    }

    @Override
    public List<ResponseData> valuation(String industry) {

        List<ResponseData> responseDataList = valuationBusiness.business(industry);

        responseDataList = stockScore.scoring(responseDataList);
        return responseDataList;
    }
}
