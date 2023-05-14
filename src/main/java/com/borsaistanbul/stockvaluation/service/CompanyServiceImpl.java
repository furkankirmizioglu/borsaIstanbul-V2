package com.borsaistanbul.stockvaluation.service;

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
    public List<String> listAll() {

        List<String> response = companyInfoRepository.fetchAllIndustries();

        if (response.isEmpty()) {
            // Read Excel file.
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
                        case 0 -> info.setTicker(cell.getStringCellValue());
                        case 1 -> info.setCompanyName(cell.getStringCellValue());
                        case 2 -> info.setIndustry(cell.getStringCellValue());
                        default -> {
                        }
                    }
                }
                toSaveList.add(info);
            }
            companyInfoRepository.saveAll(toSaveList);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
