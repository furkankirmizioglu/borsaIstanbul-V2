package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;

import java.util.List;

public interface ValuationService {

    List<ValuationResult> valuation(String industry);

}
