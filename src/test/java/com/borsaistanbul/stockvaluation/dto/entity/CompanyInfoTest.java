package com.borsaistanbul.stockvaluation.dto.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CompanyInfoTest {

    private CompanyInfo companyInfo;

    @BeforeEach
    void init() {
        companyInfo = new CompanyInfo();
        companyInfo.setGuid(123L);
        companyInfo.setLastUpdated(20230818L);
        companyInfo.setTicker("TEST");
        companyInfo.setTitle("TEST");
        companyInfo.setIndustry("TEST");

    }

    @Test
    void test() {

        assertEquals("TEST", companyInfo.getTicker());
        assertEquals(123L, companyInfo.getGuid());
        assertEquals(20230818L, companyInfo.getLastUpdated());
        assertEquals("TEST", companyInfo.getTicker());
        assertEquals("TEST", companyInfo.getTitle());
        assertEquals("TEST", companyInfo.getIndustry());

    }

}
