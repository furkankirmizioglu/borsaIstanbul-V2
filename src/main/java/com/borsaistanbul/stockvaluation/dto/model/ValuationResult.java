package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValuationResult {
    private String ticker;
    private String companyName;
    private String latestBalanceSheetTerm;
    private double price;
    private double PB;
    private double PEG;
    private double longTermDebtToEquity;
    private double finalScore;
    private String suggestion;
}
