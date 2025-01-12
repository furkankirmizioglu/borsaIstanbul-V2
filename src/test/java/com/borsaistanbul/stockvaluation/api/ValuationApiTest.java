package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.service.ScoringService;
import com.borsaistanbul.stockvaluation.service.ValuationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ValuationApiTest {

    private static final String TEST = "TEST";
    private static final double DEFAULT_DOUBLE = 10.0;

    MockMvc mockMvc;

    private List<ResponseData> responseDataList;

    @Mock
    ValuationService valuationService;

    @Mock
    ScoringService scoringService;

    @BeforeEach
    void init() {

        final ValuationApi valuationApi = new ValuationApi(scoringService, valuationService);
        mockMvc = MockMvcBuilders.standaloneSetup(valuationApi).build();
        responseDataList = List.of(ResponseData.builder()
                .ticker(TEST)
                .companyName(TEST)
                .latestBalanceSheetTerm(TEST)
                .price(DEFAULT_DOUBLE)
                .pe(DEFAULT_DOUBLE)
                .pb(DEFAULT_DOUBLE)
                .evToEbitda(DEFAULT_DOUBLE)
                .netDebtToEbitda(DEFAULT_DOUBLE)
                .finalScore(DEFAULT_DOUBLE)
                .suggestion(TEST)
                .build());
    }

    @Test
    @SneakyThrows
    void test() {

        when(valuationService.valuation(anyString())).thenReturn(responseDataList);
        when(scoringService.sortAndScore(anyList())).thenReturn(responseDataList);

        mockMvc.perform(get("/valuation/list")
                        .param("industry", "Otomotiv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        verify(valuationService, times(1)).valuation(anyString());
        verify(scoringService, times(1)).sortAndScore(anyList());
    }
}