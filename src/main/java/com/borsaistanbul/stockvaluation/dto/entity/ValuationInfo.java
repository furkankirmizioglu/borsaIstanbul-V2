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
    @SequenceGenerator(name = "valuation_seq", sequenceName = "valuation_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "valuation_seq")
    @Column(name = "GUID")
    private long guid;

    @Column(name = "LAST_UPDATED")
    private long lastUpdated;

    @Column(name = "TICKER")
    private String ticker;

    @Column(name = "BALANCE_SHEET_TERM")
    private String balanceSheetTerm;

    @Column(name = "EQUITY")
    private BigDecimal equity;

    @Column(name = "INITIAL_CAPITAL")
    private BigDecimal initialCapital;

    @Column(name = "ANNUAL_EBITDA")
    private BigDecimal annualEbitda;

    @Column(name = "ANNUAL_NET_PROFIT")
    private BigDecimal annualNetProfit;

    @Column(name = "PREV_YEAR_NET_PROFIT")
    private BigDecimal prevYearNetProfit;

    @Column(name = "NET_DEBT")
    private BigDecimal netDebt;

    @Column(name = "TOTAL_ASSETS")
    private BigDecimal totalAssets;

    @Column(name = "LONG_TERM_LIABILITIES")
    private BigDecimal longTermLiabilities;

    @Column(name = "SHORT_TERM_LIABILITIES")
    private BigDecimal shortTermLiabilities;

}