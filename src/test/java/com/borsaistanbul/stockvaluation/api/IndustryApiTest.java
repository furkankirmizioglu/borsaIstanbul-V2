package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.service.CompanyService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndustryApi.class)
@AutoConfigureMockMvc(addFilters = false)
class IndustryApiTest {

    @Autowired
    MockMvc mockMvc;

    private List<String> industriesList;

    @MockBean
    CompanyService companyService;

    @BeforeEach
    void init() {
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
