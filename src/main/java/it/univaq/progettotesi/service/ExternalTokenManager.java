package it.univaq.progettotesi.service;

import it.univaq.progettotesi.dto.LoginRequestDTO;
import it.univaq.progettotesi.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;

@Service
public class ExternalTokenManager {

    private final WebClient webClient;
    private final String apiUsername;
    private final String apiPassword;

    private volatile String cachedToken;       // null = non presente
    private final Object lock = new Object();  // per evitare login concorrenti

    public ExternalTokenManager(
            WebClient webClient,
            @Value("${api.login.username}") String apiUsername,
            @Value("${api.login.password}") String apiPassword) {
        this.webClient = webClient;
        this.apiUsername = apiUsername;
        this.apiPassword = apiPassword;
    }

    public String getToken() {
        // Fast path
        if (cachedToken != null) return cachedToken;

        // Slow path con lock (double-checked locking)
        synchronized (lock) {
            if (cachedToken != null) return cachedToken;
            cachedToken = doLogin();
            return cachedToken;
        }
    }

    public void invalidate() {
        synchronized (lock) {
            cachedToken = null;
        }
    }

    private String doLogin() {
        LoginResponseDTO resp = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/login")
                        .queryParam("username", apiUsername)
                        .queryParam("password", apiPassword)
                        .build())
                .retrieve()
                .bodyToMono(LoginResponseDTO.class)
                .block();

        if (resp == null || resp.token() == null || resp.token().isBlank()) {
            throw new IllegalStateException("Login fallito: token mancante");
        }

        return resp.token();
    }
}
