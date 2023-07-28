package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.common.StockValuationApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Valuation API")
public interface ValuationApi {

    @Operation(
            summary = "Valuation API",
            description = "This API returns valuation results to UI.",
            operationId = "API0"
    )
    @PostMapping("/list")
    @ApiResponse(responseCode = "200", description = "Valuation successfully completed.")
    ResponseEntity<StockValuationApiResponse<ValuationApiResponse>> valuation(@RequestBody String request);

}
