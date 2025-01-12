package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.client.FintablesClient;
import com.borsaistanbul.stockvaluation.client.GetCurrentPriceResponse;
import com.borsaistanbul.stockvaluation.client.TechnicalDataClient;
import com.borsaistanbul.stockvaluation.dto.entity.CompanyInfo;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.exception.StockValuationApiException;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import com.borsaistanbul.stockvaluation.utils.CalculateTools;
import com.borsaistanbul.stockvaluation.utils.ResponseCodes;
import com.borsaistanbul.stockvaluation.utils.Utils;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.borsaistanbul.stockvaluation.utils.Constants.TRY;
import static com.borsaistanbul.stockvaluation.utils.Constants.USER_AGENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValuationServiceImpl implements ValuationService {

    private final CompanyInfoRepository companyInfoRepository;
    private final ValuationInfoRepository valuationInfoRepository;
    private final TechnicalDataClient technicalDataService;
    private final FintablesClient fintablesClient;

    @Override
    public List<ResponseData> valuation(String industry) {

        log.info("{} sektörü için değerleme hesaplamaları başladı.", industry);

        List<CompanyInfo> companies = companyInfoRepository.findAllByIndustryOrderByTicker(industry);

        List<ResponseData> responseDataList = new ArrayList<>();

        companies.forEach(company -> {

            ValuationInfo info = valuationInfoRepository.findAllByTicker(company.getTicker()).orElseGet(() -> getAndSaveFinancialData(company.getTicker()));

            double price;

            try {
                ResponseEntity<GetCurrentPriceResponse> getCurrentPriceResponse = technicalDataService.getCurrentPrice(company.getTicker());
                price = Objects.requireNonNull(getCurrentPriceResponse.getBody()).getPrice();

                responseDataList.add(ResponseData.builder()
                        .price(price)
                        .companyName(company.getTitle())
                        .ticker(company.getTicker())
                        .latestBalanceSheetTerm(info.getBalanceSheetTerm())
                        .pe(CalculateTools.priceToEarnings(price, info))
                        .pb(CalculateTools.priceToBookRatio(price, info))
                        .evToEbitda(CalculateTools.enterpriseValueToEbitda(price, info))
                        .netDebtToEbitda(CalculateTools.netDebtToEbitda(info))
                        .netCashPerShare(CalculateTools.netCashPerShare(info))
                        .build());

                log.info("{} için değerleme işlemi tamamlandı.", company.getTicker());

            } catch (Exception ex) {
                log.error("{} için değerleme hesaplanırken bir hata oluştu, bu şirket listeye dahil değildir...", company.getTicker());
            }
        });

        log.info("{} sektörü için değerleme hesaplamaları tamamlandı.", industry);
        return responseDataList;
    }

    private ValuationInfo getAndSaveFinancialData(String ticker) {
        try {
            XSSFWorkbook workbook = downloadExcelFile(ticker);
            ValuationInfo response = saveValuationInfo(ticker, workbook);
            workbook.close();
            return response;
        } catch (IOException ex) {
            throw new StockValuationApiException(ResponseCodes.UNKNOWN_ERROR, ex.getMessage());
        }
    }

    private XSSFWorkbook downloadExcelFile(String ticker) {

        try (Response response = fintablesClient.downloadFile(USER_AGENT, ticker, TRY)) {

            Path tempFile = Files.createTempFile("financial-table-", ".xlsx");

            try (InputStream inputStream = response.body().asInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }

            return new XSSFWorkbook(tempFile.toFile());
        } catch (IOException | InvalidFormatException e) {
            throw new StockValuationApiException(ResponseCodes.API_EXCEPTION, "Error downloading financial table: " + e.getMessage());
        }
    }

    private ValuationInfo saveValuationInfo(String ticker, XSSFWorkbook workbook) throws IOException {

        ValuationInfo entity = new ValuationInfo();
        balanceSheetReader(entity, workbook.getSheetAt(0));
        incomeStatementReader(entity, workbook.getSheetAt(3));
        entity.setBalanceSheetTerm(workbook.getSheetAt(0).getRow(0).getCell(1).getStringCellValue());
        entity.setTicker(ticker);
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        valuationInfoRepository.save(entity);
        workbook.close();

        log.info("{} için bilanço başarıyla kaydedildi.", ticker);
        return entity;
    }

    private void balanceSheetReader(ValuationInfo entity, XSSFSheet sheet) {

        double cashAndEquivalents = CalculateTools.getFirstCellValue(sheet.getRow(2));
        double financialInvestments = CalculateTools.getFirstCellValue(sheet.getRow(4));
        double shortTermFinancialDebts = CalculateTools.getFirstCellValue(sheet.getRow(49));
        double longTermFinancialDebts = CalculateTools.getFirstCellValue(sheet.getRow(67));
        double totalLongTermLiabilities = CalculateTools.getFirstCellValue(sheet.getRow(83));
        double equities = CalculateTools.getFirstCellValue(sheet.getRow(85));
        double initialCapital = CalculateTools.getFirstCellValue(sheet.getRow(86));

        double netDebt = (longTermFinancialDebts + shortTermFinancialDebts) - (cashAndEquivalents + financialInvestments);
        double netCash = Precision.round((cashAndEquivalents - totalLongTermLiabilities), 2);

        entity.setNetDebt(netDebt);
        entity.setEquity(equities);
        entity.setNetCash(netCash);
        entity.setInitialCapital(initialCapital);
    }

    private void incomeStatementReader(ValuationInfo entity, XSSFSheet sheet) {

        double annualGrossProfit = CalculateTools.getFirstCellValue(sheet.getRow(12));
        double administrativeExpenses = CalculateTools.getFirstCellValue(sheet.getRow(14));
        double marketingSalesDistributionExpenses = CalculateTools.getFirstCellValue(sheet.getRow(15));
        double researchDevelopmentExpenses = CalculateTools.getFirstCellValue(sheet.getRow(16));
        double annualNetProfit = CalculateTools.getFirstCellValue(sheet.getRow(47));
        double amortization = CalculateTools.getFirstCellValue(sheet.getRow(49));

        double ebitda = annualGrossProfit + administrativeExpenses + marketingSalesDistributionExpenses + researchDevelopmentExpenses + amortization;

        entity.setAnnualEbitda(ebitda);
        entity.setAnnualNetProfit(annualNetProfit);
    }
}
