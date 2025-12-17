package com.example.reporting_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${services.sales.url}")
    private String salesServiceUrl;
    
    @Value("${services.inventory.url}")
    private String inventoryServiceUrl;
    
    @Bean(name = "salesWebClient")
    public WebClient salesWebClient() {
        return WebClient.builder()
                .baseUrl(salesServiceUrl)
                .build();
    }
    
    @Bean(name = "inventoryWebClient")
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .build();
    }
}
