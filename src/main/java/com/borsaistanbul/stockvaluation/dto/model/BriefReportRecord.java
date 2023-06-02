package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@Builder
public class BriefReportRecord {
    private ArrayList<String> quarterlyEbitda;
    private ArrayList<String> quarterlySales;
}
