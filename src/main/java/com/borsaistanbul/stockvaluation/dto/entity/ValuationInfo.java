package com.borsaistanbul.stockvaluation.dto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "VALUATION_INFO")
@Getter
@Setter
public class ValuationInfo {
    @Id
    @SequenceGenerator(name = "valuation_seq",
            sequenceName = "valuation_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "valuation_seq")
    private long guid;
    private long lastUpdated;
    private String ticker;
    private String balanceSheetTerm;
    private float equity;
    private float mainEquity;
    private float initialCapital;
    private float prevInitialCapital;
    private float longTermLiabilities;
    private float ttmNetProfit;
    private float prevTtmNetProfit;

}
