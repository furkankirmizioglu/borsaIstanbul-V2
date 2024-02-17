package com.borsaistanbul.stockvaluation.client;

import com.borsaistanbul.stockvaluation.exception.StockValuationApiException;
import com.borsaistanbul.stockvaluation.utils.ResponseCodes;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class TechnicalDataServiceImpl implements TechnicalDataService {
    @Override
    public HashMap<String, Double> fetchTechnicalData(String ticker) {

        // I developed an API that returns current price for stock on Python.
        // It handles incoming requests from another port and sends its response.
        String url = "http://localhost:8090/price?ticker=" + ticker;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            // Extract the price value from the JSON response

            HashMap<String, Double> priceInfo = new HashMap<>();
            priceInfo.put("price", Double.parseDouble(new JSONObject(responseEntity.getBody()).get("price").toString()));

            return priceInfo;
        } catch (ResourceAccessException ex) {
            throw new StockValuationApiException(ResponseCodes.API_EXCEPTION,
                    "An error occured while retrieving stock price: " + ex.getMessage(),
                    null);
        }

    }
}
