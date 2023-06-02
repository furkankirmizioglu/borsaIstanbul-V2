package com.borsaistanbul.stockvaluation.repository;

import com.borsaistanbul.stockvaluation.dto.entity.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {

    @Query("SELECT DISTINCT C.industry FROM CompanyInfo C ORDER BY C.industry")
    List<String> fetchAllIndustries();

    @Query("SELECT C.ticker from CompanyInfo C where C.industry = ?1")
    List<String> findTickerByIndustry(String industry);

    @Query("SELECT C.title from CompanyInfo C where C.ticker = ?1")
    String findCompanyNameByTicker(String ticker);

    @Modifying
    @Transactional
    @Query("UPDATE CompanyInfo C SET C.title = ?2 where C.ticker = ?1")
    void updateTitle(String code, String title);
}
