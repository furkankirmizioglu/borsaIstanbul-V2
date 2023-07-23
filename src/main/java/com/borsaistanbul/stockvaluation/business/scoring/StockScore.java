package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.Response;

import java.util.List;

public interface StockScore {

    public void pegScore(List<Response> resultList);
    public void pbScore(List<Response> resultList);
    public void ebitdaMarginScore(List<Response> resultList);
    public void netProfitMarginScore(List<Response> resultList);
    public void scoring(List<Response> resultList);

}
