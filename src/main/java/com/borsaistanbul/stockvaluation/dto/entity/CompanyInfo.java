package com.borsaistanbul.stockvaluation.dto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "COMPANY_INFO")
public class CompanyInfo {
    @Id
    @SequenceGenerator(name = "company_seq",
            sequenceName = "company_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "company_seq")
    @Column(name = "GUID")
    private long guid;

    @Column(name = "LAST_UPDATED")
    private long lastUpdated;

    @Column(name = "TICKER")
    private String ticker;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "INDUSTRY")
    private String industry;


}
