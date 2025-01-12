package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.dto.model.ResponseData;
import com.borsaistanbul.stockvaluation.service.ScoringService;
import com.borsaistanbul.stockvaluation.service.ValuationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Valuation API")
@RestController
@RequestMapping(path = "/valuation")
@RequiredArgsConstructor
public class ValuationApi {

    private final ScoringService scoringService;
    private final ValuationService valuationService;

    @Operation(
            summary = "Valuation API",
            description = "This API returns valuation results to the page.",
            operationId = "API0"
    )
    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Valuation completed successfully.")
    public ResponseEntity<List<ResponseData>> valuation(@RequestParam String industry) {

        List<ResponseData> responseDataList = valuationService.valuation(industry);
        return ResponseEntity.ok(scoringService.sortAndScore(responseDataList));
    }
}