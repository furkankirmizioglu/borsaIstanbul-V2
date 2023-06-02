package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.dto.model.BalanceSheetRecord;
import com.borsaistanbul.stockvaluation.dto.model.BriefReportRecord;
import com.borsaistanbul.stockvaluation.dto.model.ValuationResult;

import java.util.List;

public interface ValuationBusiness {

    List<ValuationResult> business(List<String> tickerList, String industry);

    void webScraper(String ticker, String industry);

    BriefReportRecord getBriefReportData(String ticker);

    void saveValuationInfo(String ticker, String balanceSheetTerm, List<BalanceSheetRecord> balanceSheetRecordList, BriefReportRecord briefReportRecord);

    void saveValuationInfoBanking(String ticker, String balanceSheetTerm, List<BalanceSheetRecord> balanceSheetRecordList);

    void saveValuationInfoInsurance(String ticker, String balanceSheetTerm, List<BalanceSheetRecord> balanceSheetRecordList);

}
