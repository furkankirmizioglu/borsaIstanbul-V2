package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    private CompanyService companyService;

    @Mock
    private CompanyInfoRepository companyInfoRepository;

    @BeforeEach
    void init() {
        companyService = new CompanyServiceImpl(companyInfoRepository);
    }

    @Test
    void listAllNotFoundOnDb() {
        when(companyInfoRepository.saveAll(anyList())).thenReturn(null);
        List<String> response = companyService.listAll();
        assertNotNull(response);
        verify(companyInfoRepository, times(1)).saveAll(anyList());
    }
}
