package com.borsaistanbul.stockvaluation.dto.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Company {

    private String name;
    private String ticker;
    private String industry;
    private String latestBalanceSheetTerm;
    @Nullable
    private float equity;
    @Nullable
    private float mainEquity;
    @Nullable
    private float initialCapital;
    @Nullable
    private float previousInitialCapital;
    @Nullable
    private float longTermLiabilities;
    @Nullable
    private float ttmNetProfit;
    @Nullable
    private float previousTtmNetProfit;
    @Nullable
    private List<Float> priceInfoArray;
    @Nullable
    private float closePrice;


}
