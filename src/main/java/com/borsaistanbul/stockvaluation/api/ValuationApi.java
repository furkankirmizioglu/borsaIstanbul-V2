package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.service.ValuationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Valuation API")
@RestController
@RequestMapping(path = "/valuation")
@RequiredArgsConstructor
public class ValuationApi {

    private final ValuationService service;

    @Operation(
            summary = "Valuation API",
            description = "This API returns valuation results to UI.",
            operationId = "API0"
    )
    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Valuation successfully completed.")
    public ResponseEntity<List<ResponseData>> valuation(@RequestParam String industry) {
        return ResponseEntity.ok(service.valuation(industry));
    }
}