package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;

import java.util.List;

public interface StockScore {

    public void pegScore(List<ValuationResult> resultList);
    public void pbScore(List<ValuationResult> resultList);
    public void ebitdaMarginScore(List<ValuationResult> resultList);
    public void netProfitMarginScore(List<ValuationResult> resultList);
    public void scoring(List<ValuationResult> resultList);

}
