package com.borsaistanbul.stockvaluation.business.scoring;

import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class StockScoreImpl implements StockScore {

    // Scoring based on PEG ratio.
    public void pegScore(List<ValuationResult> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getPeg));
        for (ValuationResult x : resultList) {
            if (x.getPeg() > 0) {
                x.setFinalScore(scoreCounter);
                scoreCounter--;
            } else {
                x.setFinalScore(0);
            }
        }
    }

    // Scoring based on P/B ratio.
    public void pbScore(List<ValuationResult> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getPb));
        for (ValuationResult x : resultList) {
            if (x.getPb() > 0) {
                x.setFinalScore(x.getFinalScore() + scoreCounter);
                scoreCounter--;
            }
        }
    }

    // Scoring based on EBITDA Margin sort by highest to lowest.
    public void ebitdaMarginScore(List<ValuationResult> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getEbitdaMargin).reversed());
        for (ValuationResult x : resultList) {
            if (x.getEbitdaMargin() != Double.POSITIVE_INFINITY && x.getEbitdaMargin() != Double.NEGATIVE_INFINITY) {
                x.setFinalScore(x.getFinalScore() + scoreCounter);
                scoreCounter--;
            }
        }
    }

    // Scoring based on Net Profit Margin sort by highest to lowest.
    public void netProfitMarginScore(List<ValuationResult> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getNetProfitMargin).reversed());
        for (ValuationResult x : resultList) {
            if (x.getNetProfitMargin() != Double.POSITIVE_INFINITY && x.getNetProfitMargin() != Double.NEGATIVE_INFINITY) {
                x.setFinalScore(x.getFinalScore() + scoreCounter);
                scoreCounter--;
            }
        }
    }

    // Scoring based on net debt to ebitda ratio.
    public void netDebtToEbitdaScore(List<ValuationResult> resultList) {
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getNetDebtToEbitda));
        for (ValuationResult x : resultList) {
            x.setFinalScore(x.getFinalScore() + scoreCounter);
            scoreCounter--;
        }
    }

    public void scoring(List<ValuationResult> resultList) {

        pegScore(resultList);
        pbScore(resultList);
        ebitdaMarginScore(resultList);
        netProfitMarginScore(resultList);
        netDebtToEbitdaScore(resultList);

        // Total score will divide to count of companies multiply by indicators (5) count and index to 100.
        resultList.sort(Comparator.comparing(ValuationResult::getFinalScore));
        for (ValuationResult x : resultList) {
            double score = Precision.round(x.getFinalScore() / (resultList.size() * 5) * 100, 0);
            x.setFinalScore(score);
        }

        // Total scores will sort by highest to lowest.
        resultList.sort(Comparator.comparing(ValuationResult::getFinalScore).reversed());
        for (ValuationResult x : resultList) {
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
