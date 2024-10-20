package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.enums.Suggestion;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class StockScoreImpl implements StockScore {

    // Scoring based on Price/Book ratio.
    public void pbScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getPb));
        resultList.forEach(x -> {
            if (x.getPb() > 0) {
                x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x)));
            }
        });
    }

    // Scoring based on Enterprise Value / EBITDA ratio.
    public void evToEbitdaScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getEvToEbitda));
        resultList.forEach(x ->
        {
            if (x.getEvToEbitda() < 40) {
                x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x)));
            }
        });
    }

    // Scoring based on Net Debt / EBITDA ratio.
    public void netDebtToEbitdaScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getNetDebtToEbitda));
        resultList.forEach(x -> x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x))));
    }

    // Scoring based on Net Debt / EBITDA ratio.
    public void netCashPerShareScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getNetCashPerShare).reversed());
        resultList.forEach(x -> x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x))));
    }

    public List<ResponseData> scoring(List<ResponseData> resultList) {

        pbScore(resultList);
        evToEbitdaScore(resultList);
        netDebtToEbitdaScore(resultList);
        netCashPerShareScore(resultList);

        // Total score will divide to list size multiply by number of indicators (4) count and index to 100.
        resultList.forEach(x -> {
            x.setFinalScore(Precision.round(x.getFinalScore() / (resultList.size() * 4) * 100, 0));
            x.setSuggestion(makeSuggestion(x.getFinalScore()));
        });

        // Total scores will sort by highest to lowest.
        resultList.sort(Comparator.comparing(ResponseData::getFinalScore).reversed());
        return resultList;
    }

    private String makeSuggestion(double score) {
        if (score >= 85) {
            return (Suggestion.STRONG_BUY.label);
        } else if (score >= 70) {
            return (Suggestion.BUY.label);
        } else if (score >= 55) {
            return (Suggestion.NEUTRAL.label);
        } else if (score >= 40) {
            return (Suggestion.SELL.label);
        } else {
            return (Suggestion.STRONG_SELL.label);
        }
    }

}