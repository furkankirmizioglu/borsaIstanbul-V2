package com.borsaistanbul.stockvaluation.dto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
    private BigDecimal equity;
    private BigDecimal mainEquity;
    private BigDecimal initialCapital;
    private BigDecimal prevInitialCapital;
    private BigDecimal longTermLiabilities;
    private BigDecimal ttmNetProfit;
    private BigDecimal prevTtmNetProfit;

}
