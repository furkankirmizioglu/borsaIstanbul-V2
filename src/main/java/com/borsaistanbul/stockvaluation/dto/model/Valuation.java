package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Valuation {
    private String ticker;
    private double PEG;
    private double PB;
    private int finalScore;
}
