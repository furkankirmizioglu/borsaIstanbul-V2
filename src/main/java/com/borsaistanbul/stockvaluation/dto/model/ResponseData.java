package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseData {
    private String ticker;
    private String companyName;
    private String latestBalanceSheetTerm;
    private double price;
    private double pe;
    private double pb;
    private double evToEbitda;
    private double netDebtToEbitda;
    private double netCashPerShare;
    private double marketValueToNetWorkingCapital;
    private double finalScore;
    private String suggestion;
}
