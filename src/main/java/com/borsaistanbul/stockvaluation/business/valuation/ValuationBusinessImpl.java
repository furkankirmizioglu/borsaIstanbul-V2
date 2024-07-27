package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.GetCurrentPriceResponse;
import com.borsaistanbul.stockvaluation.client.TechnicalDataClient;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.FinancialValues;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.exception.StockValuationApiException;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import com.borsaistanbul.stockvaluation.utils.CalculateTools;
import com.borsaistanbul.stockvaluation.utils.Constants;
import com.borsaistanbul.stockvaluation.utils.ResponseCodes;
import com.borsaistanbul.stockvaluation.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.borsaistanbul.stockvaluation.utils.Constants.FINANCIAL_LIABILITIES;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValuationBusinessImpl implements ValuationBusiness {

    private final CompanyInfoRepository companyInfoRepository;
    private final ValuationInfoRepository valuationInfoRepository;
    private final TechnicalDataClient technicalDataService;

    private String industryInfo;

    @Override
    public List<ResponseData> business(String industry) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        industryInfo = industry;
        log.info("{} sektörü için değerleme hesaplamaları başladı...", industryInfo);

        List<String> tickerList = companyInfoRepository.findTickerByIndustry(industryInfo);

        List<ResponseData> responseDataList = new ArrayList<>();

        tickerList.forEach(ticker -> {

            String companyName = companyInfoRepository.findCompanyNameByTicker(ticker);
            Optional<ValuationInfo> info = valuationInfoRepository.findAllByTicker(ticker);

            if (info.isEmpty()) {
                info = Optional.of(getAndSaveFinancialData(ticker));
            }

            double price;

            ResponseEntity<GetCurrentPriceResponse> getCurrentPriceResponse =
                    technicalDataService.getCurrentPrice(ticker);

            if (getCurrentPriceResponse.getStatusCode().equals(HttpStatus.OK)) {
                price = Objects.requireNonNull(getCurrentPriceResponse.getBody()).getPrice();
            } else {
                throw new StockValuationApiException("99", "Fiyat bilgisi getirilemedi.", null);
            }

            info.ifPresent(valuationInfo -> responseDataList.add(
                    ResponseData.builder()
                            .price(price)
                            .companyName(companyName)
                            .ticker(ticker)
                            .latestBalanceSheetTerm(valuationInfo.getBalanceSheetTerm())
                            .pe(CalculateTools.priceToEarnings(price, valuationInfo))
                            .pb(CalculateTools.priceToBookRatio(price, valuationInfo))
                            .enterpriseValueToEbitda(CalculateTools.enterpriseValueToEbitda(price, valuationInfo))
                            .netDebtToEbitda(CalculateTools.netDebtToEbitda(valuationInfo))
                            .debtToEquity(CalculateTools.debtToEquity(valuationInfo))
                            .build()));

            log.info("{} için değerleme işlemi tamamlandı...", ticker);
        });

        stopWatch.stop();
        log.info("{} sektörü için değerleme hesaplamaları {} saniyede tamamlandı...", industryInfo,
                Precision.round((float) stopWatch.getTime() / 1000, 2));

        log.info("Hisse başı ortalama hesaplama süresi: {} saniye...",
                Precision.round((float) stopWatch.getTime() / 1000 / tickerList.size(), 2));

        return responseDataList;
    }

    private ValuationInfo getAndSaveFinancialData(String ticker) {
        try {
            XSSFWorkbook workbook = getExcelFile(ticker);
            ValuationInfo response = saveValuationInfo(ticker, workbook);
            workbook.close();
            return response;
        } catch (IOException ex) {
            throw new StockValuationApiException(ResponseCodes.UNKNOWN_ERROR, ex.getMessage(), null);
        }

    }

    private static XSSFWorkbook getExcelFile(String ticker) {
        try {
            URL url = ResourceUtils.toURL(MessageFormat.format(Constants.FINTABLES, ticker));
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            InputStream inputStream = uc.getInputStream();

            String fileName = "report.xlsx";
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            fileOutputStream.close();
            return new XSSFWorkbook(fileName);
        } catch (IOException e) {
            throw new StockValuationApiException(ResponseCodes.API_EXCEPTION, e.getMessage(), null);
        }
    }

    private ValuationInfo saveValuationInfo(String ticker, XSSFWorkbook workbook) throws IOException {

        FinancialValues values = new FinancialValues();

        XSSFSheet balanceSheet = workbook.getSheetAt(0);
        XSSFSheet annualProfitSheet = workbook.getSheetAt(3);

        balanceSheetParser(values, balanceSheet);
        annualProfitSheetParser(values, annualProfitSheet);

        values.nullToZeroConverter();

        ValuationInfo entity = new ValuationInfo();
        entity.setAnnualEbitda(CalculateTools.ebitda(values));

        if (industryInfo.equals(Constants.HOLDINGS)) {
            entity.setAnnualSales(values.getAnnualSales().add(values.getIncomeFromOtherFields()));
        } else {
            entity.setAnnualSales(values.getAnnualSales());
        }

        entity.setBalanceSheetTerm(balanceSheet.getRow(0).getCell(1).getStringCellValue());
        entity.setEquity(values.getEquities());
        entity.setInitialCapital(values.getInitialCapital());
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        entity.setNetDebt(CalculateTools.netDebt(values));
        entity.setPrevYearNetProfit(values.getPrevYearNetProfit());
        entity.setAnnualNetProfit(values.getAnnualNetProfit());
        entity.setTicker(ticker);
        entity.setTotalAssets(values.getTotalAssets());
        entity.setLongTermLiabilities(values.getTotalLongTermLiabilities());
        entity.setShortTermLiabilities(values.getTotalShortTermLiabilities());
        valuationInfoRepository.save(entity);

        workbook.close();

        log.info("{} için bilanço başarıyla kaydedildi.", ticker);

        return entity;
    }

    private void balanceSheetParser(FinancialValues values, XSSFSheet balanceSheet) {

        for (int j = 0; j < balanceSheet.getPhysicalNumberOfRows(); j++) {
            Row row = balanceSheet.getRow(j);
            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case FINANCIAL_LIABILITIES -> {
                        if (Objects.equals(values.getTotalFinancialLiabilities(), null))
                            values.setTotalFinancialLiabilities(CalculateTools.cellValue(row, 1));
                        else
                            values.setTotalFinancialLiabilities(
                                    values.getTotalFinancialLiabilities().add(CalculateTools.cellValue(row, 1)));
                    }
                    case Constants.CASH_AND_EQUIVALENTS ->
                            values.setCashAndEquivalents(CalculateTools.cellValue(row, 1));
                    case Constants.FINANCIAL_INVESTMENTS -> {
                        if (Objects.equals(values.getFinancialInvestments(), null)) {
                            values.setFinancialInvestments(CalculateTools.cellValue(row, 1));
                        }
                    }
                    case Constants.EQUITIES -> values.setEquities(CalculateTools.cellValue(row, 1));
                    case Constants.INITIAL_CAPITAL -> values.setInitialCapital(CalculateTools.cellValue(row, 1));
                    case Constants.TOTAL_ASSETS -> values.setTotalAssets(CalculateTools.cellValue(row, 1));
                    case Constants.TOTAL_LONG_TERM_LIABILITIES ->
                            values.setTotalLongTermLiabilities(CalculateTools.cellValue(row, 1));
                    case Constants.TOTAL_SHORT_TERM_LIABILITIES ->
                            values.setTotalShortTermLiabilities(CalculateTools.cellValue(row, 1));
                    default -> {
                        // no need to any operation
                    }
                }
            }
        }
    }

    private void annualProfitSheetParser(FinancialValues values, XSSFSheet annualProfitSheet) {
        for (int i = 0; i < annualProfitSheet.getPhysicalNumberOfRows(); i++) {
            Row row = annualProfitSheet.getRow(i);
            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case Constants.INCOME_FROM_SALES -> values.setAnnualSales(CalculateTools.cellValue(row, 1));
                    case Constants.INCOME_FROM_OTHER_FIELDS ->
                            values.setIncomeFromOtherFields(CalculateTools.cellValue(row, 1));
                    case Constants.GROSS_PROFIT -> values.setAnnualGrossProfit(CalculateTools.cellValue(row, 1));
                    case Constants.ADMINISTRATIVE_EXPENSES ->
                            values.setAdministrativeExpenses(CalculateTools.cellValue(row, 1));
                    case Constants.MARKETING_SALES_DISTRIBUTION_EXPENSES ->
                            values.setMarketingSalesDistributionExpenses(CalculateTools.cellValue(row, 1));
                    case Constants.RESEARCH_DEVELOPMENT_EXPENSES ->
                            values.setResearchDevelopmentExpenses(CalculateTools.cellValue(row, 1));
                    case Constants.PARENT_COMPANY_SHARES -> {
                        values.setAnnualNetProfit(CalculateTools.cellValue(row, 1));
                        values.setPrevYearNetProfit(CalculateTools.cellValue(row, 5));
                    }
                    case Constants.AMORTIZATION -> values.setAmortization(CalculateTools.cellValue(row, 1));
                    default -> {
                        // no need to any operation
                    }
                }
            }
        }

    }
}
