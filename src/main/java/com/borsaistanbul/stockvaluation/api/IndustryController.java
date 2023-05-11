package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "/industry")
public class IndustryController {

    private final CompanyService service;

    @Autowired
    public IndustryController(CompanyService service) {
        this.service = service;
    }

    @GetMapping(path = "list")
    private List<String> fetchAllIndustries() {
        List<String> response = service.fetchAllIndustries();
        return response;

    }
}
