package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.Valuation;

import java.util.List;

public interface ValuationService {

    List<Valuation> valuation(String industry);

}
