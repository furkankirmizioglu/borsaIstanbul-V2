package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.Valuation;
import com.borsaistanbul.stockvaluation.service.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "/valuation")
public class ValuationController {

    private final ValuationService service;

    @Autowired
    public ValuationController(ValuationService service) {
        this.service = service;
    }


    @PostMapping(path = "list")
    private List<Valuation> valuation(@RequestBody String industry) {
        List<Valuation> response = service.valuation(industry);
        return response;

    }
}
