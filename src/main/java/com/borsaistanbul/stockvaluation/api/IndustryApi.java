package com.borsaistanbul.stockvaluation.api;

import com.borsaistanbul.stockvaluation.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Industry API")
@RestController
@RequestMapping(path = "/industry")
@RequiredArgsConstructor
public class IndustryApi {

    private final CompanyService service;

    @Operation(
            summary = "Industry API",
            description = "This API returns industry list.",
            operationId = "API1"
    )
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "Industry list fetched successfully.")
    public ResponseEntity<List<String>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }
}
