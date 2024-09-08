package com.borsaistanbul.stockvaluation.business.valuation;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

            ValuationInfo info = valuationInfoRepository.findAllByTicker(company.getTicker()).orElseGet(() -> getAndSaveFinancialData(company.getTicker()));

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
                    .build());

            log.info("{} için değerleme işlemi tamamlandı.", company.getTicker());
        });

        log.info("{} sektörü için değerleme hesaplamaları tamamlandı.", industry);
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
        double equities = CalculateTools.getFirstCellValue(sheet.getRow(85));
        double initialCapital = CalculateTools.getFirstCellValue(sheet.getRow(86));

        double netDebt = (longTermFinancialDebts + shortTermFinancialDebts) - (cashAndEquivalents + financialInvestments);

        entity.setNetDebt(netDebt);
        entity.setEquity(equities);
        entity.setInitialCapital(initialCapital);
    }

    private void incomeStatementReader(ValuationInfo entity, XSSFSheet sheet) {

        double annualGrossProfit = CalculateTools.getFirstCellValue(sheet.getRow(11));
        double administrativeExpenses = CalculateTools.getFirstCellValue(sheet.getRow(13));
        double marketingSalesDistributionExpenses = CalculateTools.getFirstCellValue(sheet.getRow(14));
        double researchDevelopmentExpenses = CalculateTools.getFirstCellValue(sheet.getRow(15));
        double annualNetProfit = CalculateTools.getFirstCellValue(sheet.getRow(46));
        double amortization = CalculateTools.getFirstCellValue(sheet.getRow(48));

        double ebitda = annualGrossProfit + administrativeExpenses + marketingSalesDistributionExpenses + researchDevelopmentExpenses + amortization;

        entity.setAnnualEbitda(ebitda);
        entity.setAnnualNetProfit(annualNetProfit);
    }
}