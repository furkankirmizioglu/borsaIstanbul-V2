package com.borsaistanbul.stockvaluation.client;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PriceInfoServiceImpl implements PriceInfoService {
    @Override
    public double fetchPriceInfo(String ticker) {
        // I created an API that returns current price for stock on Python.
        // It handles incoming requests from another port and sends its response.
        String url = "http://localhost:" + 8090 + "/price?ticker=" + ticker;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        // Extract the price value from the JSON response
        return Double.parseDouble(new JSONObject(responseEntity.getBody()).get("price").toString());
    }
}
