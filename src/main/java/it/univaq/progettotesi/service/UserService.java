package it.univaq.progettotesi.service;

import it.univaq.progettotesi.entity.Admin;
import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.Client;
import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.repository.AdminRepository;
import it.univaq.progettotesi.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final AdminRepository AdminRepository;
    private final ClientRepository ClientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AdminRepository AdminRepository, ClientRepository ClientRepository, PasswordEncoder passwordEncoder) {
        this.AdminRepository = AdminRepository;
        this.ClientRepository = ClientRepository;
        this.passwordEncoder = passwordEncoder;
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

    public Admin createAdmin(String name, String surname, String email, String password) {
        password = passwordEncoder.encode(password);
        Admin b = new Admin(name, surname, email, password);
        return AdminRepository.save(b);
    }

    public Admin saveAdmin(Admin admin) {
        return AdminRepository.save(admin);
    }

    public Admin updateAdmin(Long id, String name, String surname) {
        Admin b = AdminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User non trovato: " + id));
        b.setName(name);
        b.setSurname(surname);
        return AdminRepository.save(b);
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

    public Client createClient(String name, String surname, String email, String password,  Building building) {
        password = passwordEncoder.encode(password);
        Client c = new Client(name, surname, email, password, building);
        return ClientRepository.save(c);
    }

    public Client saveClient(Client client) {
        return ClientRepository.save(client);
    }

    public Client updateClient(Long id, String name, String surname) {
        Client c = ClientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client non trovato: " + id));
        c.setName(name);
        c.setSurname(surname);
        return ClientRepository.save(c);
    }

    public void deleteClient(Long id) {
        ClientRepository.deleteById(id);
    }



}
