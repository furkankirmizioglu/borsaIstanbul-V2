package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface ValuationBusiness {

    List<ValuationResult> business(List<String> tickerList, String industry);

    void webScraper(String ticker, String industry);

    void saveValuationInfo(String ticker, XSSFWorkbook workbook);

    void saveValuationInfoBanking(String ticker, XSSFWorkbook workbook);

    void saveValuationInfoInsurance(String ticker, XSSFWorkbook workbook);

}
