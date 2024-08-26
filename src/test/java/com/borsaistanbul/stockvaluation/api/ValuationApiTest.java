package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.service.ValuationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ValuationApi.class)
@AutoConfigureMockMvc(addFilters = false)
class ValuationApiTest {

    private static final String TEST = "TEST";
    private static final double DEFAULT_DOUBLE = 10.0;

    @Autowired
    MockMvc mockMvc;

    private List<ResponseData> responseDataList;

    @MockBean
    ValuationService valuationService;

    @BeforeEach
    void init() {
        responseDataList = new ArrayList<>();
        responseDataList.add(ResponseData.builder()
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

        mockMvc.perform(get("/valuation/list")
                        .param("industry", "Otomotiv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        verify(valuationService, times(1)).valuation(anyString());
    }
}