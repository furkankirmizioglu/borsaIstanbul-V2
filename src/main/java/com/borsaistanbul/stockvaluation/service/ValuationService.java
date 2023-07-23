package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.Response;

import java.util.List;

public interface ValuationService {
    List<Response> valuation(String industry);

}
