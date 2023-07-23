package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Response {
    private String ticker;
    private String companyName;
    private String latestBalanceSheetTerm;
    private double price;
    private double pe;
    private double pb;
    private double peg;
    private double ebitdaMargin;
    private double netProfitMargin;
    private double netDebtToEbitda;
    private double finalScore;
    private String suggestion;
}
