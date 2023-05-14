package com.borsaistanbul.stockvaluation.dto.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Builder
public class BalanceSheetRecord {

    private String label;
    private int level;
    private ArrayList<String> values;
    private ArrayList<String> quarter_values;

}
