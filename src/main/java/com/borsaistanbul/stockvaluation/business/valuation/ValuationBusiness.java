package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;

import java.util.List;

public interface ValuationBusiness {

    List<ResponseData> business(String industry);

}
