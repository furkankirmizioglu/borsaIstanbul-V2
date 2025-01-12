package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;

import java.util.List;

public interface ScoringService {

    List<ResponseData> sortAndScore(List<ResponseData> resultList);

}
