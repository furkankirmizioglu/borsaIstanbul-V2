package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ValuationApiResponse {
    List<ResponseData> responseDataList;
}
