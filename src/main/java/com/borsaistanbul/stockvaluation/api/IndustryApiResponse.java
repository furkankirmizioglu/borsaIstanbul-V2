package com.borsaistanbul.stockvaluation.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class IndustryApiResponse {

    List<String> industryList;

}
