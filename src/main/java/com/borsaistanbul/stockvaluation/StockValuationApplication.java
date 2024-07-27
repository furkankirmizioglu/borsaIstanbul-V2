package com.borsaistanbul.stockvaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class StockValuationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockValuationApplication.class, args);
	}
}
