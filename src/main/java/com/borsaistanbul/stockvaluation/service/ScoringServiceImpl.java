package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.borsaistanbul.stockvaluation.dto.enums.Suggestion.*;

@Service
public class ScoringServiceImpl implements ScoringService {

    @Override
    public List<ResponseData> sortAndScore(List<ResponseData> resultList) {

        if (Objects.isNull(resultList) || resultList.isEmpty()) {
            return resultList;
        }

        Map<String, Comparator<ResponseData>> criteria = Map.of(
                "evToEbitda", Comparator.comparing(ResponseData::getEvToEbitda),
                "priceToBook", Comparator.comparing(ResponseData::getPb),
                "netDebtToEbitda", Comparator.comparing(ResponseData::getNetDebtToEbitda),
                "netCashPerShare", Comparator.comparing(ResponseData::getNetCashPerShare));

        Map<String, Map<ResponseData, Integer>> indexMaps = new HashMap<>();

        criteria.forEach((key, comparator) -> {
            List<ResponseData> sortedList = new ArrayList<>(resultList);
            sortedList.sort(comparator);
            indexMaps.put(key, createIndexMap(sortedList));
        });

        int criteriaCount = criteria.size();
        int listSize = resultList.size();

        resultList.forEach(info -> {
            double finalScore = indexMaps.values().stream().mapToInt(map -> listSize - map.get(info)).sum();
            double percentageScore = (finalScore / (listSize * criteriaCount)) * 100;
            info.setFinalScore(Precision.round(percentageScore, 0));
            info.setSuggestion(makeSuggestion(info.getFinalScore()));
        });

        resultList.sort(Comparator.comparing(ResponseData::getFinalScore).reversed());

        return resultList;
    }

    private Map<ResponseData, Integer> createIndexMap(List<ResponseData> sortedList) {
        Map<ResponseData, Integer> indexMap = new HashMap<>();
        sortedList.forEach(element -> indexMap.put(element, sortedList.indexOf(element)));
        return indexMap;
    }

    private String makeSuggestion(double score) {
        if (score >= 85) {
            return (STRONG_BUY.label);
        } else if (score >= 70) {
            return (BUY.label);
        } else if (score >= 55) {
            return (NEUTRAL.label);
        } else if (score >= 40) {
            return (SELL.label);
        } else {
            return (STRONG_SELL.label);
        }
    }
}
