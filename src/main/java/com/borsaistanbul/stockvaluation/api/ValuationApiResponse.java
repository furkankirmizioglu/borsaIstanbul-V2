package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ValuationApiResponse {
    List<ResponseData> responseDataList;
}
