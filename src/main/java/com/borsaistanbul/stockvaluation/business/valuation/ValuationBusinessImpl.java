package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.PriceInfoService;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import com.borsaistanbul.stockvaluation.utils.Constants;
import com.borsaistanbul.stockvaluation.utils.Utils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ValuationBusinessImpl implements ValuationBusiness {
    @Autowired
    private final CompanyInfoRepository companyInfoRepository;
    @Autowired
    private final ValuationInfoRepository valuationInfoRepository;
    @Autowired
    private final PriceInfoService priceInfoService;

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
                            .netDebtToEbitda(Utils.netDebtToEbitda(valuationInfo))
                            .netProfitMargin(Utils.netProfitMargin(valuationInfo)).build()));
        }
        return valuationResultList;
    }

    @Override
    public void webScraper(String ticker, String industry) {


        try {
            String urlStr = "https://fintables.com/sirketler/" + ticker + "/finansal-tablolar/excel?currency=";
            URL url = new URL(urlStr);
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            InputStream inputStream = uc.getInputStream();

            // Save the Excel file to a local file.
            String fileName = "report.xlsx";
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.close();


            // Parse the Excel file.
            XSSFWorkbook workbook = new XSSFWorkbook(fileName);


            if (industry.equals("Bankacılık") || industry.equals("Aracı Kurumlar")) {
                saveValuationInfoBanking(ticker, workbook);
            } else if (industry.equals("Sigorta")) {
                saveValuationInfoInsurance(ticker, workbook);
            } else {
                saveValuationInfo(ticker, workbook);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void saveValuationInfo(String ticker,
                                  XSSFWorkbook workbook) {

        ValuationInfo entity = new ValuationInfo();

        BigDecimal cashAndEquivalents = BigDecimal.ZERO;
        BigDecimal financialInvestments = BigDecimal.ZERO;
        BigDecimal totalFinancialLiabilities = BigDecimal.ZERO;

        BigDecimal grossProfit = BigDecimal.ZERO;
        BigDecimal administrativeExpenses = BigDecimal.ZERO;
        BigDecimal marketingSalesDistributionExpenses = BigDecimal.ZERO;
        BigDecimal researchDevelopmentExpenses = BigDecimal.ZERO;

        BigDecimal depreciationAmortization = BigDecimal.ZERO;

        XSSFSheet balanceSheet = workbook.getSheetAt(0);
        XSSFSheet annualProfitSheet = workbook.getSheetAt(3);
        XSSFSheet annualCashFlowSheet = workbook.getSheetAt(6);

        for (int j = 0; j < balanceSheet.getPhysicalNumberOfRows(); j++) {

            Row row = balanceSheet.getRow(j);

            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case Constants.FINANCIAL_LIABILITIES ->
                            totalFinancialLiabilities = totalFinancialLiabilities.add(Utils.cellValue(row, 1));
                    case Constants.CASH_AND_EQUIVALENTS -> cashAndEquivalents = Utils.cellValue(row, 1);
                    case Constants.FINANCIAL_INVESTMENTS -> {
                        if(Objects.equals(financialInvestments, BigDecimal.ZERO))
                        {
                            financialInvestments = Utils.cellValue(row, 1);
                        }
                    }
                    case Constants.EQUITIES -> entity.setEquity(Utils.cellValue(row, 1));
                    case Constants.INITIAL_CAPITAL -> entity.setInitialCapital(Utils.cellValue(row, 1));
                }
            }
        }

        for (int i = 0; i < annualProfitSheet.getPhysicalNumberOfRows(); i++) {
            Row row = annualProfitSheet.getRow(i);
            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case Constants.INCOME_FROM_SALES -> entity.setAnnualSales(Utils.cellValue(row, 1));
                    case Constants.GROSS_PROFIT -> grossProfit = Utils.cellValue(row, 1);
                    case Constants.ADMINISTRATIVE_EXPENSES -> administrativeExpenses = Utils.cellValue(row, 1);
                    case Constants.MARKETING_SALES_DISTRIBUTION_EXPENSES ->
                            marketingSalesDistributionExpenses = Utils.cellValue(row, 1);
                    case Constants.RESEARCH_DEVELOPMENT_EXPENSES ->
                            researchDevelopmentExpenses = Utils.cellValue(row, 1);
                    case Constants.PARENT_COMPANY_SHARES -> {
                        entity.setTtmNetProfit(Utils.cellValue(row, 1));
                        entity.setPrevTtmNetProfit(Utils.cellValue(row, 5));
                    }
                }
            }
        }

        for (int k = 0; k < annualCashFlowSheet.getPhysicalNumberOfRows(); k++) {
            Row row = annualCashFlowSheet.getRow(k);
            if (row.getCell(0) != null && row.getCell(0).getStringCellValue().trim().equals(Constants.DEPRECIATION_AND_AMORTIZATION)) {
                depreciationAmortization = Utils.cellValue(row, 1);
            }
        }

        BigDecimal netDebt = totalFinancialLiabilities.subtract(cashAndEquivalents.add(financialInvestments));

        BigDecimal annualEbitda = grossProfit.add(administrativeExpenses).add(marketingSalesDistributionExpenses)
                .add(researchDevelopmentExpenses).add(depreciationAmortization);

        entity.setBalanceSheetTerm(balanceSheet.getRow(0).getCell(1).getStringCellValue());
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        entity.setAnnualEbitda(annualEbitda);
        entity.setTicker(ticker);
        entity.setNetDebt(netDebt);
        valuationInfoRepository.save(entity);
    }

    @Override
    public void saveValuationInfoBanking(String ticker, XSSFWorkbook workbook) {

        ValuationInfo entity = new ValuationInfo();

        BigDecimal cashAndEquivalents = BigDecimal.ZERO;
        BigDecimal financialInvestments = BigDecimal.ZERO;
        BigDecimal totalFinancialLiabilities = BigDecimal.ZERO;

        BigDecimal grossProfit = BigDecimal.ZERO;
        BigDecimal administrativeExpenses = BigDecimal.ZERO;
        BigDecimal marketingSalesDistributionExpenses = BigDecimal.ZERO;
        BigDecimal researchDevelopmentExpenses = BigDecimal.ZERO;

        BigDecimal depreciationAmortization = BigDecimal.ZERO;

        XSSFSheet balanceSheet = workbook.getSheetAt(0);
        XSSFSheet annualProfitSheet = workbook.getSheetAt(3);
        XSSFSheet annualCashFlowSheet = workbook.getSheetAt(6);

        for (int j = 0; j < balanceSheet.getPhysicalNumberOfRows(); j++) {

            Row row = balanceSheet.getRow(j);

            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case Constants.FINANCIAL_LIABILITIES ->
                            totalFinancialLiabilities = totalFinancialLiabilities.add(Utils.cellValue(row, 1));
                    case Constants.CASH_AND_EQUIVALENTS -> cashAndEquivalents = Utils.cellValue(row, 1);
                    case Constants.FINANCIAL_INVESTMENTS -> {
                        if(Objects.equals(financialInvestments, BigDecimal.ZERO))
                        {
                            financialInvestments = Utils.cellValue(row, 1);
                        }
                    }
                    case Constants.EQUITIES -> entity.setEquity(Utils.cellValue(row, 1));
                    case Constants.INITIAL_CAPITAL -> entity.setInitialCapital(Utils.cellValue(row, 1));
                }
            }
        }

        for (int i = 0; i < annualProfitSheet.getPhysicalNumberOfRows(); i++) {
            Row row = annualProfitSheet.getRow(i);
            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case Constants.INCOME_FROM_SALES -> entity.setAnnualSales(Utils.cellValue(row, 1));
                    case Constants.GROSS_PROFIT -> grossProfit = Utils.cellValue(row, 1);
                    case Constants.ADMINISTRATIVE_EXPENSES -> administrativeExpenses = Utils.cellValue(row, 1);
                    case Constants.MARKETING_SALES_DISTRIBUTION_EXPENSES ->
                            marketingSalesDistributionExpenses = Utils.cellValue(row, 1);
                    case Constants.RESEARCH_DEVELOPMENT_EXPENSES ->
                            researchDevelopmentExpenses = Utils.cellValue(row, 1);
                    case Constants.PARENT_COMPANY_SHARES -> {
                        entity.setTtmNetProfit(Utils.cellValue(row, 1));
                        entity.setPrevTtmNetProfit(Utils.cellValue(row, 5));
                    }
                }
            }
        }

        for (int k = 0; k < annualCashFlowSheet.getPhysicalNumberOfRows(); k++) {
            Row row = annualCashFlowSheet.getRow(k);
            if (row.getCell(0) != null && row.getCell(0).getStringCellValue().trim().equals(Constants.DEPRECIATION_AND_AMORTIZATION)) {
                depreciationAmortization = Utils.cellValue(row, 1);
            }
        }

        BigDecimal netDebt = totalFinancialLiabilities.subtract(cashAndEquivalents.add(financialInvestments));

        BigDecimal annualEbitda = grossProfit.add(administrativeExpenses).add(marketingSalesDistributionExpenses)
                .add(researchDevelopmentExpenses).add(depreciationAmortization);

        entity.setBalanceSheetTerm(balanceSheet.getRow(0).getCell(1).getStringCellValue());
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        entity.setAnnualEbitda(annualEbitda);
        entity.setTicker(ticker);
        entity.setNetDebt(netDebt);
        valuationInfoRepository.save(entity);
    }

    @Override
    public void saveValuationInfoInsurance(String ticker, XSSFWorkbook workbook) {

        ValuationInfo entity = new ValuationInfo();

        BigDecimal cashAndEquivalents = BigDecimal.ZERO;
        BigDecimal financialInvestments = BigDecimal.ZERO;
        BigDecimal totalFinancialLiabilities = BigDecimal.ZERO;

        BigDecimal grossProfit = BigDecimal.ZERO;
        BigDecimal administrativeExpenses = BigDecimal.ZERO;
        BigDecimal marketingSalesDistributionExpenses = BigDecimal.ZERO;
        BigDecimal researchDevelopmentExpenses = BigDecimal.ZERO;

        BigDecimal depreciationAmortization = BigDecimal.ZERO;

        XSSFSheet balanceSheet = workbook.getSheetAt(0);
        XSSFSheet annualProfitSheet = workbook.getSheetAt(3);
        XSSFSheet annualCashFlowSheet = workbook.getSheetAt(6);

        for (int j = 0; j < balanceSheet.getPhysicalNumberOfRows(); j++) {

            Row row = balanceSheet.getRow(j);

            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case Constants.FINANCIAL_LIABILITIES ->
                            totalFinancialLiabilities = totalFinancialLiabilities.add(Utils.cellValue(row, 1));
                    case Constants.CASH_AND_EQUIVALENTS -> cashAndEquivalents = Utils.cellValue(row, 1);
                    case Constants.FINANCIAL_INVESTMENTS -> {
                        if(Objects.equals(financialInvestments, BigDecimal.ZERO))
                        {
                            financialInvestments = Utils.cellValue(row, 1);
                        }
                    }
                    case Constants.EQUITIES -> entity.setEquity(Utils.cellValue(row, 1));
                    case Constants.INITIAL_CAPITAL -> entity.setInitialCapital(Utils.cellValue(row, 1));
                }
            }
        }

        for (int i = 0; i < annualProfitSheet.getPhysicalNumberOfRows(); i++) {
            Row row = annualProfitSheet.getRow(i);
            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case Constants.INCOME_FROM_SALES -> entity.setAnnualSales(Utils.cellValue(row, 1));
                    case Constants.GROSS_PROFIT -> grossProfit = Utils.cellValue(row, 1);
                    case Constants.ADMINISTRATIVE_EXPENSES -> administrativeExpenses = Utils.cellValue(row, 1);
                    case Constants.MARKETING_SALES_DISTRIBUTION_EXPENSES ->
                            marketingSalesDistributionExpenses = Utils.cellValue(row, 1);
                    case Constants.RESEARCH_DEVELOPMENT_EXPENSES ->
                            researchDevelopmentExpenses = Utils.cellValue(row, 1);
                    case Constants.PARENT_COMPANY_SHARES -> {
                        entity.setTtmNetProfit(Utils.cellValue(row, 1));
                        entity.setPrevTtmNetProfit(Utils.cellValue(row, 5));
                    }
                }
            }
        }

        for (int k = 0; k < annualCashFlowSheet.getPhysicalNumberOfRows(); k++) {
            Row row = annualCashFlowSheet.getRow(k);
            if (row.getCell(0) != null && row.getCell(0).getStringCellValue().trim().equals(Constants.DEPRECIATION_AND_AMORTIZATION)) {
                depreciationAmortization = Utils.cellValue(row, 1);
            }
        }

        BigDecimal netDebt = totalFinancialLiabilities.subtract(cashAndEquivalents.add(financialInvestments));

        BigDecimal annualEbitda = grossProfit.add(administrativeExpenses).add(marketingSalesDistributionExpenses)
                .add(researchDevelopmentExpenses).add(depreciationAmortization);

        entity.setBalanceSheetTerm(balanceSheet.getRow(0).getCell(1).getStringCellValue());
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        entity.setAnnualEbitda(annualEbitda);
        entity.setTicker(ticker);
        entity.setNetDebt(netDebt);
        valuationInfoRepository.save(entity);
    }
}
