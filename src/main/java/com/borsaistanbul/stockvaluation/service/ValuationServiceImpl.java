package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.BalanceSheetRecord;
import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import com.borsaistanbul.stockvaluation.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.math3.util.Precision;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    private void createValuationInfoEntity(String ticker,
                                           String balanceSheetTerm,
                                           List<BalanceSheetRecord> balanceSheetRecordList) {

        ValuationInfo entity = new ValuationInfo();
        for (BalanceSheetRecord r : balanceSheetRecordList) {
            switch (r.getLabel()) {
                case "Özkaynaklar" -> entity.setEquity(Utils.stringToBigDecimal(r.getValues().get(0)));
                case "Ana Ortaklığa Ait Özkaynaklar" ->
                        entity.setMainEquity(Utils.stringToBigDecimal(r.getValues().get(0)));
                case "Ödenmiş Sermaye" -> {
                    entity.setInitialCapital(Utils.stringToBigDecimal(r.getValues().get(0)));
                    entity.setPrevInitialCapital(Utils.stringToBigDecimal(r.getValues().get(4)));
                }
                case "Uzun Vadeli Yükümlülükler" ->
                        entity.setLongTermLiabilities(Utils.stringToBigDecimal(r.getValues().get(0)));
                case "Dönem Net Kar/Zararı" -> {
                    BigDecimal netProfit1 = Utils.stringToBigDecimal(r.getQuarter_values().get(0));
                    BigDecimal netProfit2 = Utils.stringToBigDecimal(r.getQuarter_values().get(1));
                    BigDecimal netProfit3 = Utils.stringToBigDecimal(r.getQuarter_values().get(2));
                    BigDecimal netProfit4 = Utils.stringToBigDecimal(r.getQuarter_values().get(3));
                    BigDecimal netProfit5 = Utils.stringToBigDecimal(r.getQuarter_values().get(4));

                    entity.setTtmNetProfit(netProfit1.add(netProfit2.add(netProfit3.add(netProfit4))));
                    entity.setPrevTtmNetProfit(netProfit2.add(netProfit3.add(netProfit4.add(netProfit5))));
                }
            }
        }

        entity.setTicker(ticker);
        entity.setBalanceSheetTerm(balanceSheetTerm);
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        valuationInfoRepository.save(entity);
    }

    private void webScraper(String ticker) {
        try {
            Gson gson = new Gson();
            List<BalanceSheetRecord> balanceSheetRecordList = new ArrayList<>();

            // Concat the default url with stock ticker to initialize target URL.
            String balanceSheetUrl = "https://fintables.com/sirketler/" + ticker + "/finansal-tablolar/bilanco";

            // Connect to the source to retrieve balance sheet information.
            Document doc = Jsoup.connect(balanceSheetUrl).timeout(10000).get();

            // Balance sheet values will return in a script named "__NEXT_DATA__"
            // Convert this script into a JSON file to parse the values.
            JSONArray balanceSheetRows = new JSONObject(doc.select("#__NEXT_DATA__").get(0).data())
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("data")
                    .getJSONObject("balance")
                    .getJSONArray("rows");

            // Iterating the jsonArray to set the values into an arraylist type of ValuationRecord
            for (int x = 0; x < balanceSheetRows.length(); x++) {
                balanceSheetRecordList.add(gson.fromJson(balanceSheetRows.get(x).toString(), BalanceSheetRecord.class));
            }

            // Fetch the balance sheet term from doc object.
            JSONObject bsTerm = new JSONObject(doc.select("#__NEXT_DATA__").get(0).data())
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("data")
                    .getJSONObject("balance")
                    .getJSONArray("periods")
                    .getJSONObject(0);

            String balanceSheetTerm = bsTerm.get("year") + "/" + bsTerm.get("month");

            // Pass the valuation record list and save the necessary data to database.
            createValuationInfoEntity(ticker, balanceSheetTerm, balanceSheetRecordList);

        } catch (IOException ex) {
            // TODO -> I need a type of exception for valuationService.
            throw new RuntimeException(ex);
        }
    }

    private double fetchCurrentPrice(String ticker) {

        // I created an API that returns current price for stock on Python.
        // It handles incoming requests from another port and sends its response.
        String url = "http://localhost:" + 8090 + "/price?ticker=" + ticker;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        // Extract the price value from the JSON response
        return Double.parseDouble(new JSONObject(responseEntity.getBody()).get("price").toString());

    }

    private List<ValuationResult> scoring(List<ValuationResult> resultList) {

        // Scoring based on PEG ratio.
        int scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getPEG));
        for (ValuationResult x : resultList) {
            int score;
            if (x.getPEG() > 0) {
                score = scoreCounter;
                scoreCounter--;
            } else {
                score = 0;
            }
            x.setFinalScore(score);
        }
        // Scoring based on P/B ratio.
        scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getPB));
        for (ValuationResult x : resultList) {
            double score = x.getFinalScore();
            if (x.getPB() > 0) {
                score += scoreCounter;
                scoreCounter--;
            }
            x.setFinalScore(score);
        }

        // Scoring based on Long Term Debt To Equity.
        scoreCounter = resultList.size();
        resultList.sort(Comparator.comparing(ValuationResult::getLongTermDebtToEquity));
        for (ValuationResult x : resultList) {
            double score = x.getFinalScore();
            if (x.getLongTermDebtToEquity() > 0) {
                score += scoreCounter;
                scoreCounter--;
            }
            x.setFinalScore(score);
        }

        // Total score will divide to count of companies multiply by indicators count and index to 100.
        resultList.sort(Comparator.comparing(ValuationResult::getFinalScore));
        for (ValuationResult x : resultList) {
            double score = Precision.round(x.getFinalScore() / (resultList.size() * 3) * 100, 2);
            x.setFinalScore(score);
        }

        // Total scores will sort by highest to lowest.
        resultList.sort(Comparator.comparing(ValuationResult::getFinalScore).reversed());
        for (ValuationResult x : resultList) {
            if (x.getFinalScore() >= 80) {
                x.setSuggestion("Güçlü Al");
            } else if (x.getFinalScore() >= 70) {
                x.setSuggestion("Al");
            } else if (x.getFinalScore() >= 50) {
                x.setSuggestion("Nötr");
            } else {
                x.setSuggestion("Sat");
            }
        }
        return resultList;
    }

    @Override
    public List<ValuationResult> valuation(String industry) {

        List<ValuationResult> valuationResultList = new ArrayList<>();
        JsonObject object = new Gson().fromJson(industry, JsonObject.class);

        // Find all tickers matches with given industry info.
        List<String> tickerList = companyInfoRepository.findTickerByIndustry(object.get("industry").getAsString());

        for (String ticker : tickerList) {

            // Query VALUATION_INFO table for valuation information.
            Optional<ValuationInfo> info = valuationInfoRepository.findAllByTicker(ticker);

            // If response is null,
            // then we have to fetch balance sheet data from FinTables, insert to database and inquiry again.
            if (info.isEmpty()) {
                webScraper(ticker);
                info = valuationInfoRepository.findAllByTicker(ticker);
            }

            // Retrieve the last price from FinTables.
            double price = fetchCurrentPrice(ticker);
            // Query company name from COMPANY_INFO table.
            String companyName = companyInfoRepository.findCompanyNameByTicker(ticker);

            // Valuation results will put into arraylist.
            info.ifPresent(valuationInfo -> valuationResultList.add(
                    ValuationResult.builder()
                            .ticker(valuationInfo.getTicker())
                            .price(price)
                            .companyName(companyName)
                            .latestBalanceSheetTerm(valuationInfo.getBalanceSheetTerm())
                            .PB(Utils.priceToBookRatio(price, valuationInfo))
                            .PEG(Utils.priceToEarningsGrowth(price, valuationInfo))
                            .longTermDebtToEquity(Utils.longTermDebtToEquity(valuationInfo))
                            .build()));
        }

        // Sort the valuationResultList by finalScore and send the response to UI.
        return scoring(valuationResultList);
    }
}
