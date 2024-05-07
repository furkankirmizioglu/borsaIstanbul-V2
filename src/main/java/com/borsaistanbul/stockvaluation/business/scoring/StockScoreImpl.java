package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.utils.Constants;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class StockScoreImpl implements StockScore {

    // Scoring based on PEG ratio.
    public void pegScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getPeg));
        resultList.forEach(x -> {
            if (x.getPeg() > 0) {
                x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x)));
            }
        });
    }

    // Scoring based on P/B ratio.
    public void pbScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getPb));
        resultList.forEach(x -> {
            if (x.getPb() > 0) {
                x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x)));
            }
        });
    }

    // Scoring based on EBITDA Margin sort by highest to lowest.
    public void ebitdaMarginScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getEbitdaMargin).reversed());
        resultList.forEach(x -> {
            if (x.getEbitdaMargin() != Double.POSITIVE_INFINITY && x.getEbitdaMargin() != Double.NEGATIVE_INFINITY) {
                x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x)));
            }
        });
    }

    // Scoring based on Net Profit Margin sort by highest to lowest.
    public void netProfitMarginScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getNetProfitMargin).reversed());
        resultList.forEach(x -> {
            if (x.getNetProfitMargin() != Double.POSITIVE_INFINITY && x.getNetProfitMargin() != Double.NEGATIVE_INFINITY) {
                x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x)));
            }
        });
    }

    // Scoring based on net debt to ebitda ratio.
    public void netDebtToEbitdaScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getNetDebtToEbitda));
        resultList.forEach(x -> x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x))));
    }

    public void leverageRatioScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getLeverageRatio));
        resultList.forEach(x -> x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x))));
    }

    public List<ResponseData> scoring(List<ResponseData> resultList) {
        pegScore(resultList);
        pbScore(resultList);
        ebitdaMarginScore(resultList);
        netProfitMarginScore(resultList);
        netDebtToEbitdaScore(resultList);
        leverageRatioScore(resultList);

        // Total score will divide to list size multiply by indicators (6) count and index to 100.
        resultList.forEach(x -> {
            x.setFinalScore(Precision.round(x.getFinalScore() / (resultList.size() * 6) * 100, 0));
            if (x.getFinalScore() >= 85) {
                x.setSuggestion(Constants.STRONG_BUY);
            } else if (x.getFinalScore() >= 70) {
                x.setSuggestion(Constants.BUY);
            } else if (x.getFinalScore() >= 55) {
                x.setSuggestion(Constants.NEUTRAL);
            } else if (x.getFinalScore() >= 40) {
                x.setSuggestion(Constants.SELL);
            } else {
                x.setSuggestion(Constants.STRONG_SELL);
            }
        });

        // Total scores will sort by highest to lowest.
        resultList.sort(Comparator.comparing(ResponseData::getFinalScore).reversed());
        return resultList;
    }
}