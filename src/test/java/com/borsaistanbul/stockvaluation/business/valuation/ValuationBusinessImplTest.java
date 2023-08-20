package com.borsaistanbul.stockvaluation.business.valuation;

import com.borsaistanbul.stockvaluation.client.PriceInfoService;
import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import com.borsaistanbul.stockvaluation.repository.ValuationInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class ValuationBusinessImplTest {


    @Mock
    private CompanyInfoRepository companyInfoRepository;

    @Autowired
    private ValuationInfoRepository valuationInfoRepository;

    @Autowired
    private PriceInfoService priceInfoService;

    private ValuationBusinessImpl valuationBusiness;

    private String industry;

    private final List<String> tickerList = new ArrayList<>();

    private ValuationInfo valuationInfo1;

    @BeforeEach
    void init() {
        companyInfoRepository = Mockito.mock(CompanyInfoRepository.class);
        valuationInfoRepository = Mockito.mock(ValuationInfoRepository.class);
        priceInfoService = Mockito.mock(PriceInfoService.class);
        valuationBusiness = new ValuationBusinessImpl(companyInfoRepository, valuationInfoRepository, priceInfoService);
        industry = "Bankacılık";

        tickerList.add("GARAN");
        tickerList.add("YKBNK");


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

}
