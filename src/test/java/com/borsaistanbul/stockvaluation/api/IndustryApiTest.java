package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.service.CompanyService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IndustryApiTest {

    private List<String> industriesList;

    private MockMvc mockMvc;

    @Mock
    CompanyService companyService;

    @BeforeEach
    void init() {

        final IndustryApi industryApi = new IndustryApi(companyService);

        mockMvc = MockMvcBuilders.standaloneSetup(industryApi).build();


        industriesList = new ArrayList<>();
        industriesList.add("Bankacılık");
        industriesList.add("Otomotiv");
        industriesList.add("Holding");
    }

    @Test
    @SneakyThrows
    void test() {




        when(companyService.listAll()).thenReturn(industriesList);

        mockMvc.perform(get("/industry/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        verify(companyService, times(1)).listAll();
    }
}