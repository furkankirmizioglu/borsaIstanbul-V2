package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.Valuation;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ValuationServiceImpl implements ValuationService {

    @Autowired
    private final CompanyInfoRepository companyInfoRepository;
    @Autowired
    private final ValuationInfoRepository valuationInfoRepository;

    public ValuationServiceImpl(CompanyInfoRepository companyInfoRepository, ValuationInfoRepository valuationInfoRepository) {
        this.companyInfoRepository = companyInfoRepository;
        this.valuationInfoRepository = valuationInfoRepository;
    }

    private double priceToEarningsGrowth(double closePrice, ValuationInfo info) {
        double EPS_2 = info.getTtmNetProfit() / info.getInitialCapital();
        double EPS_1 = info.getPrevTtmNetProfit() / info.getPrevInitialCapital();
        double EPS_GROWTH_RATE = EPS_2 / EPS_1 - 1;
        double PE = closePrice / EPS_2;
        double priceToEarningsGrowth = PE / EPS_GROWTH_RATE;
        return priceToEarningsGrowth;
    }

    private double priceToBookRatio(double closePrice, ValuationInfo info) {
        double marketValue = info.getInitialCapital() * closePrice;
        double priceToBookRatio = marketValue / info.getMainEquity();
        return priceToBookRatio;
    }

    private void webScrapingFunc(String ticker) {

        try {
            ValuationInfo info = new ValuationInfo();
            info.setTicker(ticker);

            Document doc;
            String url = "https://fintables.com/sirketler/" + ticker + "/finansal-tablolar/bilanco";
            doc = Jsoup.connect(url).get();
            Elements rows = doc.getAllElements();
            for (Element tr : rows) {
            }

        } catch (IOException ex) {
        }
    }

    @Override
    public List<Valuation> valuation(String industry) {

        List<Valuation> valuations = new ArrayList<>();

        // Find all tickers matches with given industry info.
        List<String> tickerList = companyInfoRepository.findTickerByIndustry(industry);

        for (String ticker : tickerList) {

            //Go to VALUATION_INFO table for valuation information.
            Optional<ValuationInfo> info = valuationInfoRepository.findAllByTicker(ticker);

            if (info.isEmpty()) {
                // TODO -> If doesn't exist, than we've to do web scraping.
                webScrapingFunc(ticker);
            }

            // TODO -> Calculate the PE, PEG, and PB ratios.
            valuations.add(Valuation.builder()
                    .ticker(info.get().getTicker())
                    .PB(priceToBookRatio(12.5, info.get()))
                    .PEG(priceToEarningsGrowth(12.5, info.get()))
                    .build());

            // TODO -> Score the tickers sort on their values.
            Collections.sort(valuations, Comparator.comparing(Valuation::getPEG));

        }

        return valuations;
    }
}
