package it.univaq.progettotesi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.progettotesi.dto.LoginRequestDTO;
import it.univaq.progettotesi.dto.LoginResponseDTO;
import it.univaq.progettotesi.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalCommunicationService {

    private final WebClient webClient;
    private final ExternalTokenManager tokenManager;
    private static final Logger log = LoggerFactory.getLogger(ExternalCommunicationService.class);

    public ExternalCommunicationService(WebClient webClient, ExternalTokenManager tokenManager) {
        this.webClient = webClient;
        this.tokenManager = tokenManager;
    }

    public void saveUserDTO(UserDTO user) {
        executeWithBearerRetry(() ->
                webClient.post()
                        .uri("/api/userdata/save")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenManager.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                        .retrieve()
                        .toBodilessEntity()
                        .block()
        );
    }

    public void updateUserDTO(UserDTO user, String oldUsername) {
        executeWithBearerRetry(() ->
                webClient.put()
                        .uri("/api/userdata/update/{username}", oldUsername)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenManager.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                        .retrieve()
                        .toBodilessEntity()
                        .block()
        );
    }

    // Esegue la chiamata; se prende 401, invalida token, rifà login e ritenta una volta.
    private void executeWithBearerRetry(Runnable call) {
        try {
            call.run();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized e) {
            log.info("401 ricevuto: invalido token e ritento una volta");
            tokenManager.invalidate();
            // retry una sola volta
            call.run();
        }
    }
}
