package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.common.StockValuationApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Industry API")
public interface IndustryApi {

    @Operation(
            summary = "Industry API",
            description = "This API returns industry list.",
            operationId = "API1"
    )
    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Listing industries has successfully completed.")
    ResponseEntity<StockValuationApiResponse<IndustryApiResponse>> listAll(IndustryApiRequest request);

}
