package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.dto.ClientDTO;
import it.univaq.progettotesi.entity.Client;
import it.univaq.progettotesi.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buildings")
public class ClientRestController {

    private final ClientRepository clientRepository;

    public ClientRestController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/{buildingId}/clients")
    public Page<ClientDTO> getClients(@PathVariable Long buildingId, Pageable pageable) {
        return clientRepository.findByBuildingId(buildingId, pageable)
                .map(this::toDTO);
    }

    private ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getSurname(),
                client.getEmail(),
                client.getBirthDate(),
                client.getCellphone()
        );
    }
}
