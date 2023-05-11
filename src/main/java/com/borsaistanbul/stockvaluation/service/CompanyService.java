package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.Company;
import java.util.List;

public interface CompanyService {
    List<Company> getCompanyList();
    List <String> fetchAllIndustries();
}
