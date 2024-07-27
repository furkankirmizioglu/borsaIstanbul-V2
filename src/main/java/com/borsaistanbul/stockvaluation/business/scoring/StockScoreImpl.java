package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.utils.Constants;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class StockScoreImpl implements StockScore {

    // Scoring based on Price/Earnings ratio.
    public void peScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getPe));
        resultList.forEach(x -> {
            if (x.getPe() > 0) {
                x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x)));
            }
        });
    }

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
    public void enterpriseValueToEbitdaScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getEnterpriseValueToEbitda));
        resultList.forEach(x -> x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x))));
    }

    // Scoring based on Net Debt / EBITDA ratio.
    public void netDebtToEbitdaScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getNetDebtToEbitda));
        resultList.forEach(x -> x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x))));
    }

    // Scoring based on Debt/Equity ratio.
    public void debtToEquityScore(List<ResponseData> resultList) {
        resultList.sort(Comparator.comparing(ResponseData::getDebtToEquity));
        resultList.forEach(x -> x.setFinalScore(x.getFinalScore() + (resultList.size() - resultList.indexOf(x))));
    }

    public List<ResponseData> scoring(List<ResponseData> resultList) {
        peScore(resultList);
        pbScore(resultList);
        enterpriseValueToEbitdaScore(resultList);
        netDebtToEbitdaScore(resultList);
        debtToEquityScore(resultList);

        // Total score will divide to list size multiply by number of indicators (5) count and index to 100.
        resultList.forEach(x -> {
            x.setFinalScore(Precision.round(x.getFinalScore() / (resultList.size() * 5) * 100, 0));

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