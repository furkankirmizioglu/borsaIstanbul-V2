package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.TechnicalDataService;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValuationBusinessImplTest {
    @Mock
    private CompanyInfoRepository companyInfoRepository;
    @Autowired
    ValuationInfoRepository valuationInfoRepository;
    @Autowired
    TechnicalDataService technicalDataService;
    ValuationBusinessImpl valuationBusiness;
    String industry;
    List<String> tickerList;
    ValuationInfo valuationInfo1;

    HashMap<String, Double> priceInfoHashMap;

    @BeforeEach
    void init() {
        companyInfoRepository = mock(CompanyInfoRepository.class);
        valuationInfoRepository = mock(ValuationInfoRepository.class);
        technicalDataService = mock(TechnicalDataService.class);

        valuationBusiness = new ValuationBusinessImpl(companyInfoRepository, valuationInfoRepository, technicalDataService);
        industry = "Otomotiv";

        priceInfoHashMap = new HashMap<>();
        priceInfoHashMap.put("price", 100.0);
        priceInfoHashMap.put("rsi", 25.0);

        tickerList = new ArrayList<>();
        tickerList.add("FROTO");

        valuationInfo1 = new ValuationInfo();
        valuationInfo1.setGuid(123L);
        valuationInfo1.setLastUpdated(20230818L);
        valuationInfo1.setTicker("TEST");
        valuationInfo1.setBalanceSheetTerm("2023/06");
        valuationInfo1.setEquity(BigDecimal.TEN);
        valuationInfo1.setInitialCapital(BigDecimal.TEN);
        valuationInfo1.setAnnualEbitda(BigDecimal.TEN);
        valuationInfo1.setAnnualSales(BigDecimal.TEN);
        valuationInfo1.setTtmNetProfit(BigDecimal.TEN);
        valuationInfo1.setPrevTtmNetProfit(new BigDecimal(5));
        valuationInfo1.setNetDebt(BigDecimal.TEN);
        valuationInfo1.setTotalAssets(BigDecimal.TEN);
        valuationInfo1.setLongTermLiabilities(BigDecimal.TEN);
        valuationInfo1.setShortTermLiabilities(BigDecimal.TEN);

    }

    @Test
    void businessTestValuationInfoFound() {

        when(companyInfoRepository.findTickerByIndustry(anyString())).thenReturn(tickerList);
        when(valuationInfoRepository.findAllByTicker(anyString())).thenReturn(Optional.of(valuationInfo1));
        when(companyInfoRepository.findCompanyNameByTicker(anyString())).thenReturn("TEST_COMPANY");
        when(technicalDataService.fetchTechnicalData(anyString())).thenReturn(priceInfoHashMap);

        List<ResponseData> responseDataList = valuationBusiness.business(industry);

        Assertions.assertNotNull(responseDataList.get(0));

    }

    @Test
    @SneakyThrows
    void businessTestValuationInfoNotFound() {

        when(companyInfoRepository.findTickerByIndustry(anyString())).thenReturn(tickerList);
        when(companyInfoRepository.findCompanyNameByTicker(anyString())).thenReturn("TEST_COMPANY");
        when(technicalDataService.fetchTechnicalData(anyString())).thenReturn(priceInfoHashMap);

        // TODO -> You have to mock the URL so it shouldn't go to FinTables actually.
        when(valuationInfoRepository.save(any(ValuationInfo.class))).thenReturn(null);
        when(valuationInfoRepository.findAllByTicker(anyString())).thenReturn(Optional.empty());

        List<ResponseData> responseDataList = valuationBusiness.business(industry);
        Assertions.assertNotNull(responseDataList.get(0));

    }

}
