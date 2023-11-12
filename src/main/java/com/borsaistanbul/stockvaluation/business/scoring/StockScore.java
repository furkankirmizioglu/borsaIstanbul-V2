package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;

import java.util.List;

public interface StockScore {
    List<ResponseData> scoring(List<ResponseData> resultList);
}
