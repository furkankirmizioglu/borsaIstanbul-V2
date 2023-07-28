package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;

import java.util.List;

public interface ValuationService {
    List<ResponseData> valuation(String industry);

}
