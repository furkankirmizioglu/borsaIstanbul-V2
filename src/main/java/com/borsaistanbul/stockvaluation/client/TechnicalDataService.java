package com.borsaistanbul.stockvaluation.client;

import java.util.HashMap;

public interface TechnicalDataService {
    HashMap<String, Double> fetchTechnicalData(String ticker);
}
