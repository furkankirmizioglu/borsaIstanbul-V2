package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.ValuationResponse;

import java.util.List;

public interface ValuationService {

    List<ValuationResponse> valuation(String industry);

}
