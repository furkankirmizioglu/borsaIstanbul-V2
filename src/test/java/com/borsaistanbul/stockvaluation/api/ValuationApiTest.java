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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ValuationApi.class)
@AutoConfigureMockMvc(addFilters = false)
class ValuationApiTest {

    @Autowired
    MockMvc mockMvc;

    private List<ResponseData> responseDataList;

    @MockBean
    ValuationService valuationService;


    @BeforeEach
    void init() {
        responseDataList = mock(ArrayList.class);

    }

    @Test
    @SneakyThrows
    void test() {


        when(valuationService.valuation(anyString())).thenReturn(responseDataList);

        mockMvc.perform(post("/valuation/list")
                        .content("{industry: 'Bankacılık'}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        verify(valuationService, times(1)).valuation(anyString());


    }


}
