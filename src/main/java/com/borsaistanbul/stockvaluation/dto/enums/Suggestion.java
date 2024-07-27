package com.borsaistanbul.stockvaluation.dto.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Suggestion {

    STRONG_BUY("Güçlü Al"),
    BUY("Al"),
    NEUTRAL("Nötr"),
    SELL("Sat"),
    STRONG_SELL("Güçlü Sat");

    public final String label;
}
