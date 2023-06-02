package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.business.scoring.StockScore;
import com.borsaistanbul.stockvaluation.business.valuation.ValuationBusiness;
import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValuationServiceImpl implements ValuationService {

    @Autowired
    private final CompanyInfoRepository companyInfoRepository;
    @Autowired
    private final ValuationBusiness valuationBusiness;
    @Autowired
    private final StockScore stockScore;


    public ValuationServiceImpl(CompanyInfoRepository companyInfoRepository,
                                ValuationBusiness valuationBusiness,
                                StockScore stockScore) {
        this.companyInfoRepository = companyInfoRepository;
        this.valuationBusiness = valuationBusiness;
        this.stockScore = stockScore;
    }


    @Override
    public List<ValuationResult> valuation(String industry) {

        // Find all tickers matches with given industry info.
        List<String> tickerList = companyInfoRepository.findTickerByIndustry(industry);

        List<ValuationResult> valuationResultList = valuationBusiness.business(tickerList, industry);

        // Sort the valuationResultList by finalScore and send the response to UI.
        stockScore.scoring(valuationResultList);
        return valuationResultList;
    }
}
