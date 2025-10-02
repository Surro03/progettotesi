package it.univaq.progettotesi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(
            @Value("${external.api.base-url}") String baseUrl) {

        return WebClient.builder()
                .baseUrl(baseUrl) // URL definito nel file di configurazione
                .build();
    }
}
