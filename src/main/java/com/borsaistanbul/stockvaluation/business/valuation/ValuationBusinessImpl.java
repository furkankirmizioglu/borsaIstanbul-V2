package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.PriceInfoService;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.BalanceSheetRecord;
import com.borsaistanbul.stockvaluation.dto.model.BriefReportRecord;
import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import com.borsaistanbul.stockvaluation.utils.Constants;
import com.borsaistanbul.stockvaluation.utils.Utils;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ValuationBusinessImpl implements ValuationBusiness {
    @Autowired
    private final CompanyInfoRepository companyInfoRepository;
    @Autowired
    private final ValuationInfoRepository valuationInfoRepository;
    @Autowired
    private final PriceInfoService priceInfoService;
    private Gson gson;

    public ValuationBusinessImpl(CompanyInfoRepository companyInfoRepository,
                                 ValuationInfoRepository valuationInfoRepository,
                                 PriceInfoService priceInfoService) {
        this.companyInfoRepository = companyInfoRepository;
        this.valuationInfoRepository = valuationInfoRepository;
        this.priceInfoService = priceInfoService;
    }


    @Override
    public List<ValuationResult> business(List<String> tickerList, String industry) {
        List<ValuationResult> valuationResultList = new ArrayList<>();
        for (String ticker : tickerList) {

            // Query VALUATION_INFO table for valuation information.
            Optional<ValuationInfo> info = valuationInfoRepository.findAllByTicker(ticker);

            // If response is null,
            // then we have to fetch balance sheet data from FinTables, insert to database and inquiry again.
            if (info.isEmpty()) {
                webScraper(ticker, industry);
                info = valuationInfoRepository.findAllByTicker(ticker);
            }

            // Retrieve the last price from FinTables.
            double price = priceInfoService.fetchPriceInfo(ticker);
            // Query company name from COMPANY_INFO table.
            String companyName = companyInfoRepository.findCompanyNameByTicker(ticker);

            // Valuation results will put into arraylist.
            info.ifPresent(valuationInfo -> valuationResultList.add(
                    ValuationResult.builder()
                            .price(price)
                            .companyName(companyName)
                            .ticker(valuationInfo.getTicker())
                            .latestBalanceSheetTerm(valuationInfo.getBalanceSheetTerm())
                            .pb(Utils.priceToBookRatio(price, valuationInfo))
                            .peg(Utils.priceToEarningsGrowth(price, valuationInfo))
                            .ebitdaMargin(Utils.ebitdaMargin(valuationInfo))
                            .netProfitMargin(Utils.netProfitMargin(valuationInfo)).build()));
        }
        return valuationResultList;
    }

    @Override
    public void webScraper(String ticker, String industry) {
        try {
            gson = new Gson();
            List<BalanceSheetRecord> balanceSheetRecordList = new ArrayList<>();

            // Concat the default url with stock ticker to initialize target URL.
            String balanceSheetUrl = "https://fintables.com/sirketler/" + ticker + "/finansal-tablolar/bilanco";

            // Connect to the source to retrieve balance sheet information.
            Document doc = Jsoup.connect(balanceSheetUrl).timeout(10000).get();

            // Balance sheet values will return in a script named "__NEXT_DATA__"
            // Convert this script into a JSON file to parse the values.
            JSONArray balanceSheetRows = new JSONObject(doc.select(Constants.NEXT_DATA).get(0).data())
                    .getJSONObject(Constants.PROPS)
                    .getJSONObject(Constants.PAGE_PROPS)
                    .getJSONObject("data")
                    .getJSONObject("balance")
                    .getJSONArray("rows");

            // Iterating the jsonArray to set the values into an arraylist type of ValuationRecord
            for (int x = 0; x < balanceSheetRows.length(); x++) {
                balanceSheetRecordList.add(gson.fromJson(balanceSheetRows.get(x).toString(), BalanceSheetRecord.class));
            }

            // Fetch the balance sheet term from doc object.
            JSONObject bsTerm = new JSONObject(doc.select(Constants.NEXT_DATA).get(0).data())
                    .getJSONObject(Constants.PROPS)
                    .getJSONObject(Constants.PAGE_PROPS)
                    .getJSONObject("data")
                    .getJSONObject("balance")
                    .getJSONArray("periods")
                    .getJSONObject(0);

            String balanceSheetTerm = bsTerm.get("year") + "/" + bsTerm.get("month");

            BriefReportRecord briefReportRecord = getBriefReportData(ticker);

            // Pass the valuation record list and save the necessary data to database based on industry.
            if (industry.equals("Bankacılık") || industry.equals("Aracı Kurumlar")) {
                saveValuationInfoBanking(ticker, balanceSheetTerm, balanceSheetRecordList);
            } else if (industry.equals("Sigorta")) {
                saveValuationInfoInsurance(ticker, balanceSheetTerm, balanceSheetRecordList);
            } else {
                saveValuationInfo(ticker, balanceSheetTerm, balanceSheetRecordList, briefReportRecord);
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public BriefReportRecord getBriefReportData(String ticker) {
        try {

            // Now I'm going to fetch the brief report data.
            String briefReportUrl = "https://fintables.com/sirketler/" + ticker;
            // Connect to the source to retrieve brief report information.
            Document briefRecDoc = Jsoup.connect(briefReportUrl).timeout(10000).get();

            // Brief report values will return in a script named "__NEXT_DATA__"
            // Convert this script into a JSON array to parse the values.
            JSONArray briefReportRows = new JSONObject(briefRecDoc.select(Constants.NEXT_DATA).get(0).data()).getJSONObject(Constants.PROPS).getJSONObject(Constants.PAGE_PROPS).getJSONArray("data").getJSONObject(1).getJSONArray("columns");

            JSONArray quarterSalesJsonArray = new JSONArray(briefReportRows.getJSONObject(0).getJSONObject("data").getJSONArray("values"));

            ArrayList<String> quarterSales = new ArrayList<>();
            for (int x = 0; x < quarterSalesJsonArray.length(); x++) {
                quarterSales.add(gson.fromJson(quarterSalesJsonArray.get(x).toString(), String.class));
            }

            JSONArray quarterEbitdaJsonArray = new JSONArray(briefReportRows.getJSONObject(1).getJSONObject("data").getJSONArray("values"));

            ArrayList<String> quarterEbitda = new ArrayList<>();
            for (int x = 0; x < quarterEbitdaJsonArray.length(); x++) {
                quarterEbitda.add(gson.fromJson(quarterEbitdaJsonArray.get(x).toString(), String.class));
            }

            return BriefReportRecord.builder().quarterlyEbitda(quarterEbitda).quarterlySales(quarterSales).build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void saveValuationInfo(String ticker,
                                  String balanceSheetTerm,
                                  List<BalanceSheetRecord> balanceSheetRecordList,
                                  BriefReportRecord briefReportRecord) {

        ValuationInfo entity = new ValuationInfo();
        for (BalanceSheetRecord r : balanceSheetRecordList) {
            switch (r.getLabel()) {
                case Constants.EQUITIES -> entity.setEquity(Utils.stringToBigDecimal(r.getValues().get(0)));
                case Constants.PAID_CAPITAL -> entity.setInitialCapital(Utils.stringToBigDecimal(r.getValues().get(0)));
                case "Dönem Net Kar/Zararı" -> {
                    ArrayList<BigDecimal> profitList = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        BigDecimal value;
                        if (i < r.getQuarter_values().size()) {
                            value = Utils.stringToBigDecimal(r.getQuarter_values().get(i));
                        } else {
                            value = BigDecimal.ZERO;
                        }
                        profitList.add(value);
                    }

                    entity.setTtmNetProfit(profitList.get(0)
                            .add(profitList.get(1)
                                    .add(profitList.get(2)
                                            .add(profitList.get(3)))));

                    entity.setPrevTtmNetProfit(profitList.get(4)
                            .add(profitList.get(5)
                                    .add(profitList.get(6)
                                            .add(profitList.get(7)))));
                }
                default -> {
                    // do nothing.
                }
            }
        }

        BigDecimal annualEbitda = Utils.stringToBigDecimal(briefReportRecord.getQuarterlyEbitda().get(4))
                .add(Utils.stringToBigDecimal(briefReportRecord.getQuarterlyEbitda().get(3)))
                .add(Utils.stringToBigDecimal(briefReportRecord.getQuarterlyEbitda().get(2)))
                .add(Utils.stringToBigDecimal(briefReportRecord.getQuarterlyEbitda().get(1)));

        BigDecimal annualNetSalesProfit = Utils.stringToBigDecimal(briefReportRecord.getQuarterlySales().get(4))
                .add(Utils.stringToBigDecimal(briefReportRecord.getQuarterlySales().get(3)))
                .add(Utils.stringToBigDecimal(briefReportRecord.getQuarterlySales().get(2)))
                .add(Utils.stringToBigDecimal(briefReportRecord.getQuarterlySales().get(1)));

        entity.setAnnualEbitda(annualEbitda);
        entity.setAnnualSalesProfit(annualNetSalesProfit);
        entity.setTicker(ticker);
        entity.setBalanceSheetTerm(balanceSheetTerm);
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        valuationInfoRepository.save(entity);
    }

    @Override
    public void saveValuationInfoBanking(String ticker,
                                         String balanceSheetTerm,
                                         List<BalanceSheetRecord> balanceSheetRecordList) {
        ValuationInfo entity = new ValuationInfo();
        for (BalanceSheetRecord r : balanceSheetRecordList) {
            switch (r.getLabel()) {
                case "Ana Ortaklığa Ait Özkaynaklar" ->
                        entity.setEquity(Utils.stringToBigDecimal(r.getValues().get(0)));
                case Constants.PAID_CAPITAL -> entity.setInitialCapital(Utils.stringToBigDecimal(r.getValues().get(0)));
                case "Dönem Net Kâr veya Zararı" -> {
                    BigDecimal netProfit1 = Utils.stringToBigDecimal(r.getQuarter_values().get(0));
                    BigDecimal netProfit2 = Utils.stringToBigDecimal(r.getQuarter_values().get(1));
                    BigDecimal netProfit3 = Utils.stringToBigDecimal(r.getQuarter_values().get(2));
                    BigDecimal netProfit4 = Utils.stringToBigDecimal(r.getQuarter_values().get(3));
                    BigDecimal netProfit5 = Utils.stringToBigDecimal(r.getQuarter_values().get(4));
                    BigDecimal netProfit6 = Utils.stringToBigDecimal(r.getQuarter_values().get(5));
                    BigDecimal netProfit7 = Utils.stringToBigDecimal(r.getQuarter_values().get(6));
                    BigDecimal netProfit8 = Utils.stringToBigDecimal(r.getQuarter_values().get(7));
                    entity.setTtmNetProfit(netProfit1.add(netProfit2.add(netProfit3.add(netProfit4))));
                    entity.setPrevTtmNetProfit(netProfit5.add(netProfit6.add(netProfit7.add(netProfit8))));
                }
                default -> {
                    // do nothing.
                }
            }
        }

        // TODO -> Bankacılıkta Favök Marjı ve Kâr Marjı olmaz, bunların değerlemesini ayırmak gerekiyor.
        entity.setAnnualSalesProfit(BigDecimal.ZERO);
        entity.setAnnualEbitda(BigDecimal.ZERO);
        entity.setTicker(ticker);
        entity.setBalanceSheetTerm(balanceSheetTerm);
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        valuationInfoRepository.save(entity);

    }

    @Override
    public void saveValuationInfoInsurance(String ticker,
                                           String balanceSheetTerm,
                                           List<BalanceSheetRecord> balanceSheetRecordList) {
        ValuationInfo entity = new ValuationInfo();
        for (BalanceSheetRecord r : balanceSheetRecordList) {
            switch (r.getLabel()) {
                case Constants.EQUITIES -> entity.setEquity(Utils.stringToBigDecimal(r.getValues().get(0)));
                case Constants.PAID_CAPITAL -> entity.setInitialCapital(Utils.stringToBigDecimal(r.getValues().get(0)));
                case "NET DÖNEM KARI (ZARARI)" -> {
                    BigDecimal netProfit1 = Utils.stringToBigDecimal(r.getQuarter_values().get(0));
                    BigDecimal netProfit2 = Utils.stringToBigDecimal(r.getQuarter_values().get(1));
                    BigDecimal netProfit3 = Utils.stringToBigDecimal(r.getQuarter_values().get(2));
                    BigDecimal netProfit4 = Utils.stringToBigDecimal(r.getQuarter_values().get(3));
                    BigDecimal netProfit5 = Utils.stringToBigDecimal(r.getQuarter_values().get(4));
                    BigDecimal netProfit6 = Utils.stringToBigDecimal(r.getQuarter_values().get(5));
                    BigDecimal netProfit7 = Utils.stringToBigDecimal(r.getQuarter_values().get(6));
                    BigDecimal netProfit8 = Utils.stringToBigDecimal(r.getQuarter_values().get(7));
                    entity.setTtmNetProfit(netProfit1.add(netProfit2.add(netProfit3.add(netProfit4))));
                    entity.setPrevTtmNetProfit(netProfit5.add(netProfit6.add(netProfit7.add(netProfit8))));
                }
                default -> {
                    // do nothing
                }
            }
        }

        // TODO -> Sigortacılıkta Favök Marjı ve Kâr Marjı olmaz, bunların değerlemesini ayırmak gerekiyor.
        entity.setAnnualSalesProfit(BigDecimal.ZERO);
        entity.setAnnualEbitda(BigDecimal.ZERO);
        entity.setTicker(ticker);
        entity.setBalanceSheetTerm(balanceSheetTerm);
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        valuationInfoRepository.save(entity);

    }
}
