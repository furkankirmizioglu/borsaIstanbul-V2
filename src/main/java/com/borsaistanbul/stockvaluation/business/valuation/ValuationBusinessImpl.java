package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.GetCurrentPriceResponse;
import com.borsaistanbul.stockvaluation.client.TechnicalDataClient;
import com.borsaistanbul.stockvaluation.dto.entity.CompanyInfo;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.FinancialValues;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.exception.StockValuationApiException;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import com.borsaistanbul.stockvaluation.utils.CalculateTools;
import com.borsaistanbul.stockvaluation.utils.ResponseCodes;
import com.borsaistanbul.stockvaluation.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.borsaistanbul.stockvaluation.utils.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValuationBusinessImpl implements ValuationBusiness {

    private final CompanyInfoRepository companyInfoRepository;
    private final ValuationInfoRepository valuationInfoRepository;
    private final TechnicalDataClient technicalDataService;

    @Override
    public List<ResponseData> business(String industry) {

        log.info("{} sektörü için değerleme hesaplamaları başladı.", industry);

        List<CompanyInfo> companies = companyInfoRepository.findAllByIndustryOrderByTicker(industry);

        List<ResponseData> responseDataList = new ArrayList<>();

        companies.forEach(company -> {

            ValuationInfo info = valuationInfoRepository.findAllByTicker(company.getTicker())
                    .orElseGet(() -> getAndSaveFinancialData(company.getTicker()));

            double price;

            try {

                ResponseEntity<GetCurrentPriceResponse> getCurrentPriceResponse = technicalDataService.getCurrentPrice(company.getTicker());
                price = Objects.requireNonNull(getCurrentPriceResponse.getBody()).getPrice();

            } catch (Exception ex) {
                throw new StockValuationApiException("E999", "Fiyat bilgisi getirilirken bir hata oluştu: " + ex.getMessage());
            }

            responseDataList.add(ResponseData.builder()
                    .price(price)
                    .companyName(company.getTitle())
                    .ticker(company.getTicker())
                    .latestBalanceSheetTerm(info.getBalanceSheetTerm())
                    .pe(CalculateTools.priceToEarnings(price, info))
                    .pb(CalculateTools.priceToBookRatio(price, info))
                    .evToEbitda(CalculateTools.enterpriseValueToEbitda(price, info))
                    .netDebtToEbitda(CalculateTools.netDebtToEbitda(info))
                    .roe(CalculateTools.returnOnEquity(info))
                    .roic(CalculateTools.roic(info))
                    .build());

            log.info("{} için değerleme işlemi tamamlandı.", company.getTicker());
        });

        return responseDataList;
    }

    private ValuationInfo getAndSaveFinancialData(String ticker) {
        try {
            XSSFWorkbook workbook = getExcelFile(ticker);
            ValuationInfo response = saveValuationInfo(ticker, workbook);
            workbook.close();
            return response;
        } catch (IOException ex) {
            throw new StockValuationApiException(ResponseCodes.UNKNOWN_ERROR, ex.getMessage());
        }

    }

    private static XSSFWorkbook getExcelFile(String ticker) {
        try {
            URL url = ResourceUtils.toURL(MessageFormat.format(FINTABLES, ticker));
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
            throw new StockValuationApiException(ResponseCodes.API_EXCEPTION, e.getMessage());
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
        entity.setBalanceSheetTerm(balanceSheet.getRow(0).getCell(1).getStringCellValue());
        entity.setEquity(values.getEquities());
        entity.setInitialCapital(values.getInitialCapital());
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        entity.setNetDebt(CalculateTools.netDebt(values));
        entity.setAnnualNetProfit(values.getAnnualNetProfit());
        entity.setTicker(ticker);
        entity.setNopat(values.getOperationalProfitBeforeTax().subtract(values.getOperationalTax()));
        entity.setInvestedCapital(values.getEquities().add(values.getLongTermFinancialDebts()).add(values.getShortTermFinancialDebts()).subtract(values.getCashAndEquivalents()));
        valuationInfoRepository.save(entity);

        workbook.close();

        log.info("{} için bilanço başarıyla kaydedildi.", ticker);

        return entity;
    }

    private void balanceSheetParser(FinancialValues values, XSSFSheet balanceSheet) {

        Iterator<Row> iterator = balanceSheet.rowIterator();
        iterator.next(); // skip header row.
        iterator.forEachRemaining(row -> {

            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case CASH_AND_EQUIVALENTS -> values.setCashAndEquivalents(CalculateTools.cellValue(row, 1));
                    case EQUITIES -> values.setEquities(CalculateTools.cellValue(row, 1));
                    case INITIAL_CAPITAL -> values.setInitialCapital(CalculateTools.cellValue(row, 1));
                    case FINANCIAL_INVESTMENTS -> {
                        if (Objects.isNull(values.getFinancialInvestments())) {
                            values.setFinancialInvestments(CalculateTools.cellValue(row, 1));
                        } else {
                            values.setFinancialInvestments(values.getFinancialInvestments().add(CalculateTools.cellValue(row, 1)));
                        }
                    }
                    case FINANCIAL_LIABILITIES -> {
                        if (Objects.isNull(values.getShortTermFinancialDebts())) {
                            values.setShortTermFinancialDebts(CalculateTools.cellValue(row, 1));
                        } else {
                            values.setLongTermFinancialDebts(CalculateTools.cellValue(row, 1));
                        }
                    }

                    default -> {
                        // no need to do anything.
                    }
                }
            }
        });
    }

    private void annualProfitSheetParser(FinancialValues values, XSSFSheet annualProfitSheet) {

        Iterator<Row> iterator = annualProfitSheet.rowIterator();
        iterator.next(); // skip header row.
        iterator.forEachRemaining(row -> {
            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case GROSS_PROFIT -> values.setAnnualGrossProfit(CalculateTools.cellValue(row, 1));
                    case ADMINISTRATIVE_EXPENSES -> values.setAdministrativeExpenses(CalculateTools.cellValue(row, 1));
                    case MARKETING_SALES_DISTRIBUTION_EXPENSES -> values.setMarketingSalesDistributionExpenses(CalculateTools.cellValue(row, 1));
                    case RESEARCH_DEVELOPMENT_EXPENSES -> values.setResearchDevelopmentExpenses(CalculateTools.cellValue(row, 1));
                    case AMORTIZATION -> values.setAmortization(CalculateTools.cellValue(row, 1));
                    case PARENT_COMPANY_SHARES -> values.setAnnualNetProfit(CalculateTools.cellValue(row, 1));
                    case OPERATIONAL_PROFIT_BEFORE_TAX -> values.setOperationalProfitBeforeTax(CalculateTools.cellValue(row, 1));
                    case TAX_FROM_OPERATIONS -> values.setOperationalTax(CalculateTools.cellValue(row, 1));
                    default -> {
                        // no need to do anything.
                    }
                }
            }
        });
    }
}