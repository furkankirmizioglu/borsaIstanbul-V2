package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.PriceInfoService;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
    public List<ResponseData> business(String industry) {

        // Find all tickers matches with given industry info.
        List<String> tickerList = companyInfoRepository.findTickerByIndustry(industry);

        List<ResponseData> responseDataList = new ArrayList<>();
        for (String ticker : tickerList) {

            // Go to VALUATION_INFO table for valuation values.
            // If response is null,
            // then we have to fetch balance sheet data from FinTables, insert into table and inquiry again.
            Optional<ValuationInfo> info = valuationInfoRepository.findAllByTicker(ticker);
            String companyName = companyInfoRepository.findCompanyNameByTicker(ticker);

            if (info.isEmpty()) {
                fetchFinancialTables(ticker, industry);
                info = valuationInfoRepository.findAllByTicker(ticker);
            }

            double price = priceInfoService.fetchPriceInfo(ticker);

            // Valuation results will put into arraylist.
            info.ifPresent(valuationInfo -> responseDataList.add(
                    ResponseData.builder()
                            .price(price)
                            .companyName(companyName)
                            .ticker(valuationInfo.getTicker())
                            .latestBalanceSheetTerm(valuationInfo.getBalanceSheetTerm())
                            .pb(CalculateTools.priceToBookRatio(price, valuationInfo))
                            .pe(CalculateTools.priceToEarnings(price, valuationInfo))
                            .peg(CalculateTools.priceToEarningsGrowth(price, valuationInfo))
                            .ebitdaMargin(CalculateTools.ebitdaMargin(valuationInfo))
                            .netDebtToEbitda(CalculateTools.netDebtToEbitda(valuationInfo))
                            .netProfitMargin(CalculateTools.netProfitMargin(valuationInfo)).build()));
        }
        return responseDataList;
    }

    private void fetchFinancialTables(String ticker, String industry) {
        log.debug("Dummy log for industry: {}", industry);
        XSSFWorkbook workbook = getExcelFile(ticker);

        saveValuationInfo(ticker, workbook);
    }

    private static XSSFWorkbook getExcelFile(String ticker) {
        try {
            URL url = ResourceUtils.toURL(MessageFormat.format(Constants.FINTABLES, ticker));
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
            return new XSSFWorkbook(fileName);
        } catch (IOException e) {
            throw new StockValuationApiException(ResponseCodes.API_EXCEPTION, e.getMessage(), null);
        }
    }

    private void saveValuationInfo(String ticker, XSSFWorkbook workbook) {

        FinancialValues values = new FinancialValues();

        XSSFSheet balanceSheet = workbook.getSheetAt(0);
        XSSFSheet annualProfitSheet = workbook.getSheetAt(3);
        XSSFSheet annualCashFlowSheet = workbook.getSheetAt(6);

        balanceSheetParser(values, balanceSheet);
        annualProfitSheetParser(values, annualProfitSheet);
        annualCashFlowSheetParser(values, annualCashFlowSheet);

        BigDecimal netDebt = values.getTotalFinancialLiabilities()
                .subtract(values.getCashAndEquivalents())
                .subtract(values.getFinancialInvestments());

        BigDecimal annualEbitda = values.getGrossProfit()
                .add(values.getAdministrativeExpenses())
                .add(values.getMarketingSalesDistributionExpenses())
                .add(values.getResearchDevelopmentExpenses())
                .add(values.getDepreciationAmortization());

        ValuationInfo entity = new ValuationInfo();
        entity.setAnnualEbitda(annualEbitda);
        entity.setAnnualSales(values.getAnnualSales());
        entity.setBalanceSheetTerm(balanceSheet.getRow(0).getCell(1).getStringCellValue());
        entity.setEquity(values.getEquities());
        entity.setInitialCapital(values.getInitialCapital());
        entity.setLastUpdated(Utils.getCurrentDateTimeAsLong());
        entity.setNetDebt(netDebt);
        entity.setPrevTtmNetProfit(values.getPrevTtmNetProfit());
        entity.setTtmNetProfit(values.getTtmNetProfit());
        entity.setTicker(ticker);
        valuationInfoRepository.save(entity);
    }

    private void balanceSheetParser(FinancialValues values, XSSFSheet balanceSheet) {

        for (int j = 0; j < balanceSheet.getPhysicalNumberOfRows(); j++) {
            Row row = balanceSheet.getRow(j);
            if (row.getCell(0) != null) {
                switch (row.getCell(0).getStringCellValue().trim()) {
                    case FINANCIAL_LIABILITIES -> values.setTotalFinancialLiabilities(
                            values.getTotalFinancialLiabilities().add(CalculateTools.cellValue(row, 1)));
                    case Constants.CASH_AND_EQUIVALENTS ->
                            values.setCashAndEquivalents(CalculateTools.cellValue(row, 1));
                    case Constants.FINANCIAL_INVESTMENTS -> {
                        if (Objects.equals(values.getFinancialInvestments(), BigDecimal.ZERO)) {
                            values.setFinancialInvestments(CalculateTools.cellValue(row, 1));
                        }
                    }
                    case Constants.EQUITIES -> values.setEquities(CalculateTools.cellValue(row, 1));
                    case Constants.INITIAL_CAPITAL -> values.setInitialCapital(CalculateTools.cellValue(row, 1));
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
                    case Constants.GROSS_PROFIT -> values.setGrossProfit(CalculateTools.cellValue(row, 1));
                    case Constants.ADMINISTRATIVE_EXPENSES ->
                            values.setAdministrativeExpenses(CalculateTools.cellValue(row, 1));
                    case Constants.MARKETING_SALES_DISTRIBUTION_EXPENSES ->
                            values.setMarketingSalesDistributionExpenses(CalculateTools.cellValue(row, 1));
                    case Constants.RESEARCH_DEVELOPMENT_EXPENSES ->
                            values.setResearchDevelopmentExpenses(CalculateTools.cellValue(row, 1));
                    case Constants.PARENT_COMPANY_SHARES -> {
                        values.setTtmNetProfit(CalculateTools.cellValue(row, 1));
                        values.setPrevTtmNetProfit(CalculateTools.cellValue(row, 5));
                    }
                    default -> {
                        // no need to any operation
                    }
                }
            }
        }

    }

    private void annualCashFlowSheetParser(FinancialValues values, XSSFSheet annualCashFlowSheet) {
        for (int k = 0; k < annualCashFlowSheet.getPhysicalNumberOfRows(); k++) {
            Row row = annualCashFlowSheet.getRow(k);
            if (row.getCell(0) != null && row.getCell(0).getStringCellValue().trim().equals(Constants.DEPRECIATION_AND_AMORTIZATION)) {
                values.setDepreciationAmortization(CalculateTools.cellValue(row, 1));
            }
        }
    }

}
