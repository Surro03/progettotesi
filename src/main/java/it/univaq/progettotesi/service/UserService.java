package it.univaq.progettotesi.service;

import it.univaq.progettotesi.dto.UserDTO;
import it.univaq.progettotesi.entity.Admin;
import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.Client;
import it.univaq.progettotesi.mapper.UserMapper;
import it.univaq.progettotesi.repository.AdminRepository;
import it.univaq.progettotesi.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final AdminRepository AdminRepository;
    private final ClientRepository ClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExternalCommunicationService ecs;
    private final UserMapper userMapper;

    public UserService(AdminRepository AdminRepository, ClientRepository ClientRepository, PasswordEncoder passwordEncoder, ExternalCommunicationService ecs,  UserMapper userMapper) {
        this.AdminRepository = AdminRepository;
        this.ClientRepository = ClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.ecs = ecs;
        this.userMapper = userMapper;

    }

    public List<Admin> findAllAdmins() {
        return AdminRepository.findAll();
    }

    public Optional<Admin> findAdminById(Long id) {
        return AdminRepository.findById(id);
    }

    public boolean existsAdminByEmail(String email) {
        return AdminRepository.existsByEmail(email);
    }

    public Optional<Admin> findAdminByEmail(String email) {
        return AdminRepository.findByEmail(email);
    }

    public Admin createAdmin(String name, String surname, String email, String password, LocalDate birthDate, String cellphone) {

        Admin b = new Admin(name, surname, email, password, birthDate, cellphone);
        System.out.println("Tentativo di salvataggio Admin...");
        b.setPassword(passwordEncoder.encode(password));
        Admin savedAdmin = AdminRepository.save(b); // sposto la chiamata fuori dal return per chiarezza
        savedAdmin.setUsername(name +  surname + savedAdmin.getId());
        AdminRepository.save(b);
        UserDTO userDTO = userMapper.toDto(b);
        userDTO.setPassword(password);
        System.out.println("Salvataggio Admin completato.");

        sendUserDTO(userDTO);//Si occupa di mandare il DTO

        return savedAdmin;
    }

    private void sendUserDTO(UserDTO userDTO) {
        try {
            System.out.println("Tentativo di invio DTO...");
            ecs.saveUserDTO(userDTO);
            System.out.println("Invio DTO completato.");
        } catch (Exception e) {
            // Se si verifica un errore, il codice entrerà qui
            System.err.println("!!! ERRORE DURANTE L'INVIO DEL DTO !!!");
            e.printStackTrace(); // <-- QUESTO STAMPERÀ L'ERRORE ESATTO NELLA CONSOLE!
        }
    }

    public Admin saveAdmin(Admin admin) {
        return AdminRepository.save(admin);
    }

    public Admin updateAdmin(Admin admin, String oldUsername, String newPassword) {
        UserDTO userDTO = userMapper.toDto(admin);
        userDTO.setPassword(newPassword);
        try {
            System.out.println("Tentativo di invio DTO...");
            ecs.updateUserDTO(userDTO, oldUsername);
            System.out.println("Invio DTO completato.");
        } catch (Exception e) {
            // Se si verifica un errore, il codice entrerà qui
            System.err.println("!!! ERRORE DURANTE L'INVIO DEL DTO !!!");
            e.printStackTrace(); // <-- QUESTO STAMPERÀ L'ERRORE ESATTO NELLA CONSOLE!
        }
        System.out.println("Tentativo di salvataggio Admin...");
        Admin updatedAdmin = AdminRepository.save(admin); // sposto la chiamata fuori dal return per chiarezza
        System.out.println("Salvataggio Admin completato.");
        return updatedAdmin;
    }

    public void deleteAdmin(Long id) {
        AdminRepository.deleteById(id);
    }

    public List<Client> findAllClients() {
        return ClientRepository.findAll();
    }

    public Optional<Client> findClientById(Long id) {
        return ClientRepository.findById(id);
    }

    public boolean existsClientByEmail(String email) {
        return ClientRepository.existsByEmail(email);
    }

    public Optional<Client> findClientByEmail(String email) {
        return ClientRepository.findByEmail(email);
    }

    public Page<Client> findByBuildingId(Long buildingId, Pageable pageable) {
        return ClientRepository.findByBuilding_Id(buildingId, pageable);
    }

    public Client createClient(String name, String surname, String email, String password,  Building building,  LocalDate birthDate, String cellphone) {
        Client c = new Client(name, surname, email, password, building, birthDate, cellphone);
        System.out.println("Tentativo di salvataggio Cliente...");
        c.setPassword(passwordEncoder.encode(password));
        Client savedClient = ClientRepository.save(c); // sposto la chiamata fuori dal return per chiarezza
        savedClient.setUsername(name +  surname + savedClient.getId());
        ClientRepository.save(c);
        System.out.println("Salvataggio Cliente completato.");
        UserDTO userDTO = userMapper.toDto(c);
        userDTO.setPassword(password);
        sendUserDTO(userDTO);

        return savedClient;
    }

    public Client saveClient(Client client) {
        return ClientRepository.save(client);
    }

    public Client updateClient(Long id, String name, String surname,  String email, String password, Building building, LocalDate birthDate, String cellphone) {
        Client c = ClientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client non trovato: " + id));
        c.setName(name);
        c.setSurname(surname);
        c.setEmail(email);
        c.setPassword(passwordEncoder.encode(password));
        c.setBuilding(building);
        c.setBirthDate(birthDate);
        c.setCellphone(cellphone);
        String oldUsername = c.getUsername();
        c.setUsername(name +  surname + c.getId());
        UserDTO userDTO = userMapper.toDto(c);
        userDTO.setPassword(password);
        try {
            System.out.println("Tentativo di invio DTO...");
            ecs.updateUserDTO(userDTO, oldUsername);
            System.out.println("Invio DTO completato.");
        } catch (Exception e) {
            // Se si verifica un errore, il codice entrerà qui
            System.err.println("!!! ERRORE DURANTE L'INVIO DEL DTO !!!");
            e.printStackTrace(); // <-- QUESTO STAMPERÀ L'ERRORE ESATTO NELLA CONSOLE!
        }
        System.out.println("Tentativo di salvataggio Admin...");
        Client updatedClient = ClientRepository.save(c);
        System.out.println("Salvataggio Admin completato.");
        return updatedClient;

    }

    public void deleteClient(Long id) {
        ClientRepository.deleteById(id);
    }


    public Optional<Client> findByClientEmail(String clientEmail) {
        return ClientRepository.findByEmail(clientEmail);
    }
}
