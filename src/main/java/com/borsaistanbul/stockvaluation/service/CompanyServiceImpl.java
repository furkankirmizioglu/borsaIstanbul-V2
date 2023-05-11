package com.borsaistanbul.stockvaluation.service;

import com.borsaistanbul.stockvaluation.dto.model.Company;
import com.borsaistanbul.stockvaluation.dto.entity.CompanyInfo;
import com.borsaistanbul.stockvaluation.repository.CompanyInfoRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyInfoRepository companyInfoRepository;

    public CompanyServiceImpl(CompanyInfoRepository companyInfoRepository) {
        this.companyInfoRepository = companyInfoRepository;
    }

    @Override
    public List<Company> getCompanyList() {
        List<CompanyInfo> output = companyInfoRepository.findAll();
        List<Company> responseList = new ArrayList<>();
        for (CompanyInfo info : output) {
            responseList.add(Company.builder()
                    .name(info.getCompanyName())
                    .latestBalanceSheetTerm("202301")
                    .ticker(info.getTicker())
                    .industry(info.getIndustry())
                    .build());
        }
        return responseList;
    }

    @Override
    public List<String> fetchAllIndustries() {

        List<String> response = companyInfoRepository.fetchAllIndustries();

        if (response.isEmpty()) {
            // Read excel file.
            // Initialize CompanyInfo object for each row.
            // Save into COMPANY_INFO table.
            // Execute the query again to get the list.
            readExcel();
            response = companyInfoRepository.fetchAllIndustries();
        }
        return response;
    }

    private void readExcel() {
        try {
            List<CompanyInfo> toSaveList = new ArrayList<>();
            String fileLocation = "D:\\Downloads\\Development\\Spring Boot\\stockvaluation\\src\\main\\resources\\static\\companies.xlsx";
            FileInputStream file = new FileInputStream(fileLocation);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                CompanyInfo info = new CompanyInfo();
                for (Cell cell : row) {
                    switch (cell.getColumnIndex()) {
                        case 0:
                            info.setTicker(cell.getStringCellValue());
                            break;
                        case 1:
                            info.setCompanyName(cell.getStringCellValue());
                            break;
                        case 2:
                            info.setIndustry(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                }
                toSaveList.add(info);
            }
            companyInfoRepository.saveAll(toSaveList);

        } catch (IOException ex) {
            return;
        }
    }

}
