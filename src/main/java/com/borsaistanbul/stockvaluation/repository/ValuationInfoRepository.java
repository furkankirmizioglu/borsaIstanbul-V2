package com.borsaistanbul.stockvaluation.repository;

import com.borsaistanbul.stockvaluation.dto.entity.ValuationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ValuationInfoRepository extends JpaRepository<ValuationInfo, Long> {


    Optional<ValuationInfo> findAllByTicker(String ticker);
    Optional<BigDecimal> findGuidByTicker(String ticker);


}
