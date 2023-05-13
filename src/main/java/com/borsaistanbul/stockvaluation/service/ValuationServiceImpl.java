package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.ValuationResponse;
import com.borsaistanbul.stockvaluation.dto.model.ValuationRecord;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.json.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
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
        double EPS_2 = info.getTtmNetProfit().doubleValue() / info.getInitialCapital().doubleValue();
        double EPS_1 = info.getPrevTtmNetProfit().doubleValue() / info.getPrevInitialCapital().doubleValue();
        double EPS_GROWTH_RATE = EPS_2 / EPS_1 - 1;
        double PE = closePrice / EPS_2;
        double priceToEarningsGrowth = PE / EPS_GROWTH_RATE;
        return priceToEarningsGrowth;
    }

    private double priceToBookRatio(double closePrice, ValuationInfo info) {
        double marketValue = info.getInitialCapital().doubleValue() * closePrice;
        double priceToBookRatio = marketValue / info.getMainEquity().doubleValue();
        return priceToBookRatio;
    }

    private void webScrapingFunc(String ticker) {

        try {

            ValuationInfo entity = new ValuationInfo();
            entity.setTicker(ticker);
            Gson gson = new Gson();
            ArrayList<ValuationRecord> recordList = new ArrayList<>();

            // Concat the default url with stock ticker to initialize target URL.
            String url = "https://fintables.com/sirketler/" + ticker + "/finansal-tablolar/bilanco";

            // Connect to the source to retrieve balance sheet information.
            Document doc = Jsoup.connect(url).timeout(10000).get();

            // Balance sheet values will return in a script named "__NEXT_DATA__"
            // Convert this script into a JSON file to parse the values.
            System.out.println(doc.select("#__NEXT_DATA__").get(0).data());

            JSONArray balanceSheetRows = new JSONObject(doc.select("#__NEXT_DATA__").get(0).data())
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("data")
                    .getJSONObject("balance")
                    .getJSONArray("rows");

            // Iterating the jsonArray to set the values into an arraylist type of ValuationRecord
            for (int x = 0; x < balanceSheetRows.length(); x++) {
                ValuationRecord record = gson.fromJson(balanceSheetRows.get(x).toString(), ValuationRecord.class);
                recordList.add(record);
            }

            for (ValuationRecord r : recordList) {
                switch (r.getLabel()) {
                    case "Özkaynaklar" -> entity.setEquity(new BigDecimal(r.getValues().get(0)));
                    case "Ana Ortaklığa Ait Özkaynaklar" -> entity.setMainEquity(new BigDecimal(r.getValues().get(0)));
                    case "Ödenmiş Sermaye" -> {
                        entity.setInitialCapital(new BigDecimal(r.getValues().get(0)));
                        entity.setPrevInitialCapital(new BigDecimal(r.getValues().get(1)));
                    }
                    case "Uzun Vadeli Yükümlülükler" ->
                            entity.setLongTermLiabilities(new BigDecimal(r.getValues().get(0)));
                    case "Dönem Net Kar/Zararı" -> {
                        entity.setTtmNetProfit(new BigDecimal(r.getValues().get(0)));
                        entity.setPrevTtmNetProfit(new BigDecimal(r.getValues().get(1)));
                    }
                }
            }

            JSONObject bsTerm = new JSONObject(doc.select("#__NEXT_DATA__").get(0).data())
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("data")
                    .getJSONObject("balance")
                    .getJSONArray("periods")
                    .getJSONObject(0);

            entity.setBalanceSheetTerm(bsTerm.get("year") + "/" + bsTerm.get("month"));
            // TODO -> You have to write a function that returns current datetime as long and
            //  pass it to the lastUpdated field of entity.

            valuationInfoRepository.save(entity);

        } catch (IOException ex) {
        }
    }

    @Override
    public List<ValuationResponse> valuation(String industry) {

        List<ValuationResponse> valuations = new ArrayList<>();

        // Find all tickers matches with given industry info.
        List<String> tickerList = companyInfoRepository.findTickerByIndustry(industry);

        for (String ticker : tickerList) {

            //Go to VALUATION_INFO table for valuation information.
            Optional<ValuationInfo> info = valuationInfoRepository.findAllByTicker(ticker);

            if (info.isEmpty()) {
                webScrapingFunc(ticker);
                info = valuationInfoRepository.findAllByTicker(ticker);
            }

            // TODO -> Calculate the PE, PEG, and PB ratios.
            // TODO -> You have to fetch the current price of stock.
            valuations.add(ValuationResponse.builder()
                    .ticker(info.get().getTicker())
                    .PB(priceToBookRatio(61.8, info.get()))
                    .PEG(priceToEarningsGrowth(61.8, info.get()))
                    .build());

            // TODO -> Score the tickers sort on their values.
            Collections.sort(valuations, Comparator.comparing(ValuationResponse::getPEG));

        }

        return valuations;
    }
}
