package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.GetCurrentPriceResponse;
import com.borsaistanbul.stockvaluation.client.TechnicalDataClient;
import com.borsaistanbul.stockvaluation.dto.entity.CompanyInfo;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValuationBusinessImplTest {

    @Mock
    private CompanyInfoRepository companyInfoRepository;

    @Mock
    ValuationInfoRepository valuationInfoRepository;

    @Mock
    TechnicalDataClient technicalDataClient;

    ValuationBusinessImpl valuationBusiness;
    String industry;
    List<CompanyInfo> companyList;
    ValuationInfo valuationInfo1;
    GetCurrentPriceResponse getCurrentPriceResponse;

    @BeforeEach
    void init() {

        valuationBusiness = new ValuationBusinessImpl(companyInfoRepository, valuationInfoRepository, technicalDataClient);
        industry = "Otomotiv";

        companyList = new ArrayList<>();

        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setTicker("FROTO");
        companyInfo.setIndustry("Otomotiv");
        companyInfo.setTitle("Ford Otosan");

        companyList.add(companyInfo);

        valuationInfo1 = new ValuationInfo();
        valuationInfo1.setGuid(123L);
        valuationInfo1.setLastUpdated(20230818L);
        valuationInfo1.setTicker("TEST");
        valuationInfo1.setBalanceSheetTerm("2023/06");
        valuationInfo1.setEquity(BigDecimal.TEN);
        valuationInfo1.setInitialCapital(BigDecimal.TEN);
        valuationInfo1.setAnnualEbitda(BigDecimal.TEN);
        valuationInfo1.setAnnualNetProfit(BigDecimal.TEN);
        valuationInfo1.setNetDebt(BigDecimal.TEN);
        valuationInfo1.setNopat(BigDecimal.TEN);
        valuationInfo1.setInvestedCapital(BigDecimal.TEN);

        getCurrentPriceResponse = new GetCurrentPriceResponse();
        getCurrentPriceResponse.setPrice(10.0);
    }

    @Test
    void businessTestValuationInfoFound() {

        when(companyInfoRepository.findAllByIndustryOrderByTicker(anyString())).thenReturn(companyList);
        when(valuationInfoRepository.findAllByTicker(anyString())).thenReturn(Optional.of(valuationInfo1));
        when(technicalDataClient.getCurrentPrice(anyString())).thenReturn(ResponseEntity.ok(getCurrentPriceResponse));

        List<ResponseData> responseDataList = valuationBusiness.business(industry);

        assertNotNull(responseDataList.getFirst());

    }

    @Test
    @SneakyThrows
    void businessTestValuationInfoNotFound() {

        when(companyInfoRepository.findAllByIndustryOrderByTicker(anyString())).thenReturn(companyList);
        when(technicalDataClient.getCurrentPrice(anyString())).thenReturn(ResponseEntity.ok(getCurrentPriceResponse));

        // TODO -> You have to mock the URL so it shouldn't actually go to FinTables.
        when(valuationInfoRepository.save(any(ValuationInfo.class))).thenReturn(null);
        when(valuationInfoRepository.findAllByTicker(anyString())).thenReturn(Optional.empty());

        List<ResponseData> responseDataList = valuationBusiness.business(industry);
        assertNotNull(responseDataList.getFirst());

    }
}
