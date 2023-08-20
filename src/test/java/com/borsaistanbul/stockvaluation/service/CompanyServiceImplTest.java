package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.mockito.Mockito.*;

class CompanyServiceImplTest {

    private CompanyService companyService;
    private CompanyInfoRepository companyInfoRepository;

    @BeforeEach
    void init() {
        companyInfoRepository = mock(CompanyInfoRepository.class);
        companyService = new CompanyServiceImpl(companyInfoRepository);
    }

    @Test
    void listAllNotFoundOnDb() {
        when(companyInfoRepository.saveAll(anyList())).thenReturn(null);
        List<String> response = companyService.listAll();
        Assertions.assertNotNull(response);
    }
}
