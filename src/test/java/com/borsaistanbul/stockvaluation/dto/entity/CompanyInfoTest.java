package com.borsaistanbul.stockvaluation.dto.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        Assertions.assertEquals("TEST", companyInfo.getTicker());
        Assertions.assertEquals(123L, companyInfo.getGuid());
        Assertions.assertEquals(20230818L, companyInfo.getLastUpdated());
        Assertions.assertEquals("TEST", companyInfo.getTicker());
        Assertions.assertEquals("TEST", companyInfo.getTitle());
        Assertions.assertEquals("TEST", companyInfo.getIndustry());

    }

}
