package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.PriceInfoService;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValuationBusinessImplTest {

    @Mock
    ResourceUtils resourceUtils;
    @Mock
    CompanyInfoRepository companyInfoRepository;
    @Autowired
    ValuationInfoRepository valuationInfoRepository;
    @Autowired
    PriceInfoService priceInfoService;
    ValuationBusinessImpl valuationBusiness;
    String industry;
    final List<String> tickerList = new ArrayList<>();
    ValuationInfo valuationInfo1;
    @Mock
    URI mockUri;
    @Mock
    URL mockUrl;
    @Mock
    URLConnection urlConnection;
    @Mock
    InputStream targetStream;

    @BeforeEach
    void init() {
        companyInfoRepository = mock(CompanyInfoRepository.class);
        valuationInfoRepository = mock(ValuationInfoRepository.class);
        priceInfoService = mock(PriceInfoService.class);
        resourceUtils = mock(ResourceUtils.class);
        urlConnection = mock(URLConnection.class);
        mockUri = mock(URI.class);
        mockUrl = mock(URL.class);

        targetStream = mock(InputStream.class);


        valuationBusiness = new ValuationBusinessImpl(companyInfoRepository, valuationInfoRepository, priceInfoService);
        industry = "Otomotiv";

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
        when(priceInfoService.fetchPriceInfo(anyString())).thenReturn(10.00);


        List<ResponseData> responseDataList = valuationBusiness.business(industry);

        Assertions.assertNotNull(responseDataList.get(0));

    }

    @Test
    @SneakyThrows
    void businessTestValuationInfoNotFound() {

        when(companyInfoRepository.findTickerByIndustry(anyString())).thenReturn(tickerList);
        when(companyInfoRepository.findCompanyNameByTicker(anyString())).thenReturn("TEST_COMPANY");
        when(priceInfoService.fetchPriceInfo(anyString())).thenReturn(10.00);

        File initialFile = new File("src/main/resources/static/unittestreport.xlsx");
        InputStream targetStream = new FileInputStream(initialFile);

        // TODO -> You have to mock the URL so it shouldn't go to FinTables actually.
        when(mockUrl.openConnection()).thenReturn(urlConnection);
        when(urlConnection.getInputStream()).thenReturn(targetStream);
        when(valuationInfoRepository.save(any(ValuationInfo.class))).thenReturn(null);
        when(valuationInfoRepository.findAllByTicker(anyString())).thenReturn(Optional.empty());

        List<ResponseData> responseDataList = valuationBusiness.business(industry);
        Assertions.assertNotNull(responseDataList.get(0));

    }

}
