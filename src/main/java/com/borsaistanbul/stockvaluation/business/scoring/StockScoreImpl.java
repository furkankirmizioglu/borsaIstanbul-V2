package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class StockScoreImpl implements StockScore {

    // Scoring based on PEG ratio.
    public void pegScore(List<ResponseData> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ResponseData::getPeg));
        for (ResponseData x : resultList) {
            if (x.getPeg() > 0) {
                x.setFinalScore(scoreCounter);
                scoreCounter--;
            } else {
                x.setFinalScore(0);
            }
        }
    }

    // Scoring based on P/B ratio.
    public void pbScore(List<ResponseData> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ResponseData::getPb));
        for (ResponseData x : resultList) {
            if (x.getPb() > 0) {
                x.setFinalScore(x.getFinalScore() + scoreCounter);
                scoreCounter--;
            }
        }
    }

    // Scoring based on EBITDA Margin sort by highest to lowest.
    public void ebitdaMarginScore(List<ResponseData> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ResponseData::getEbitdaMargin).reversed());
        for (ResponseData x : resultList) {
            if (x.getEbitdaMargin() != Double.POSITIVE_INFINITY && x.getEbitdaMargin() != Double.NEGATIVE_INFINITY) {
                x.setFinalScore(x.getFinalScore() + scoreCounter);
                scoreCounter--;
            }
        }
    }

    // Scoring based on Net Profit Margin sort by highest to lowest.
    public void netProfitMarginScore(List<ResponseData> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ResponseData::getNetProfitMargin).reversed());
        for (ResponseData x : resultList) {
            if (x.getNetProfitMargin() != Double.POSITIVE_INFINITY && x.getNetProfitMargin() != Double.NEGATIVE_INFINITY) {
                x.setFinalScore(x.getFinalScore() + scoreCounter);
                scoreCounter--;
            }
        }
    }

    // Scoring based on net debt to ebitda ratio.
    public void netDebtToEbitdaScore(List<ResponseData> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ResponseData::getNetDebtToEbitda));
        for (ResponseData x : resultList) {
            x.setFinalScore(x.getFinalScore() + scoreCounter);
            scoreCounter--;
        }
    }

    public void scoring(List<ResponseData> resultList) {

        pegScore(resultList);
        pbScore(resultList);
        ebitdaMarginScore(resultList);
        netProfitMarginScore(resultList);
        netDebtToEbitdaScore(resultList);

        // Total score will divide to count of companies multiply by indicators (5) count and index to 100.
        resultList.sort(Comparator.comparing(ResponseData::getFinalScore));
        for (ResponseData x : resultList) {
            double score = Precision.round(x.getFinalScore() / (resultList.size() * 5) * 100, 0);
            x.setFinalScore(score);
        }

        // Total scores will sort by highest to lowest.
        resultList.sort(Comparator.comparing(ResponseData::getFinalScore).reversed());
        for (ResponseData x : resultList) {
            if (x.getFinalScore() >= 85) {
                x.setSuggestion("Güçlü Al");
            } else if (x.getFinalScore() >= 70) {
                x.setSuggestion("Al");
            } else if (x.getFinalScore() >= 55) {
                x.setSuggestion("Nötr");
            } else if (x.getFinalScore() >= 40) {
                x.setSuggestion("Sat");
            } else {
                x.setSuggestion("Güçlü Sat");
            }
        }
    }

}
