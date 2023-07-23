package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.dto.model.Response;

import java.util.List;

public interface ValuationBusiness {

    List<Response> business(String industry);

}
