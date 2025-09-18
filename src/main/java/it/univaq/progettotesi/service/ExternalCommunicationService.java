package it.univaq.progettotesi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.progettotesi.config.DefaultAppProperties;
import it.univaq.progettotesi.dto.LoginRequestDTO;
import it.univaq.progettotesi.dto.LoginResponseDTO;
import it.univaq.progettotesi.dto.UserDTO;
import org.apache.juli.logging.Log;
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
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(ExternalCommunicationService.class);

    // Inietta i valori da application.properties
    @Value("${api.login.username}")
    private String apiUsername;

    @Value("${api.login.password}")
    private String apiPassword;

    // Iniezione del WebClient
    public ExternalCommunicationService(WebClient webClient,  DefaultAppProperties defaultAppProperties, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public void apiRegistration(String email, String password) {
        webClient.post()
                .uri("/auth/register")
                .body(Mono.just(email), String.class);

    }

    /**
     * Esegue il login all'API esterna e restituisce il token di autenticazione.
     */
    private String getToken() {
        System.out.println("Richiesta di un nuovo token di autenticazione...");


        LoginRequestDTO loginRequest = new LoginRequestDTO(apiUsername, apiPassword);

        LoginResponseDTO response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/login")
                        .queryParam("username", loginRequest.username())
                        .queryParam("password", loginRequest.password())
                        .build())
                .retrieve()
                .bodyToMono(LoginResponseDTO.class) // Mappa la risposta in un oggetto LoginResponseDTO
                .block(); // Attende la risposta

        if (response == null || response.token() == null) {
            throw new RuntimeException("Impossibile ottenere il token di autenticazione.");
        }

        System.out.println("Token ottenuto con successo.");
        return response.token();
    }

    public void sendUserDTO(UserDTO user) {
        System.out.println("Invio dati all'altro sistema...");
        try {
            // --- ECCO IL LOG ---
            // Converte l'oggetto DTO in una stringa JSON formattata (pretty print)
            
            String jsonBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
            log.info("Invio del seguente JSON all'API esterna:\n{}", jsonBody);
            // ------------------

        } catch (Exception e) {
            log.error("Errore durante la conversione del DTO in JSON", e);
        }

        String authToken = getToken();

        // Esegui la chiamata POST
        webClient.post()
                .uri("/api/userdata/save") // <-- L'endpoint specifico dell'altro server
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .body(Mono.just(user), UserDTO.class)
                .retrieve() // Esegui la richiesta
                .bodyToMono(String.class) // Aspettati una risposta di tipo String
                .block(); // .block() attende la fine della chiamata (usalo se la tua app non è reattiva)

        System.out.println("Dati inviati con successo!");
    }
}