package com.borsaistanbul.stockvaluation.repository;

import com.borsaistanbul.stockvaluation.dto.entity.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {

    @Query("SELECT DISTINCT C.industry FROM CompanyInfo C ORDER BY C.industry")
    List<String> fetchAllIndustries();

    List<CompanyInfo> findAllByIndustryOrderByTicker(String industry);
}
