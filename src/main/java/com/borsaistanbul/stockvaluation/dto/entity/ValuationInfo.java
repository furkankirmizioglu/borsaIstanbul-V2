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
    private double equity;

    @Column(name = "INITIAL_CAPITAL")
    private double initialCapital;

    @Column(name = "ANNUAL_EBITDA")
    private double annualEbitda;

    @Column(name = "ANNUAL_NET_PROFIT")
    private double annualNetProfit;

    @Column(name = "NET_DEBT")
    private double netDebt;
}