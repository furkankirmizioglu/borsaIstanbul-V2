package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.Response;
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
    @CrossOrigin(origins = "http://localhost:3000")
    List<Response> valuation(@RequestBody String industry) {
        return service.valuation(industry);

    }
}
