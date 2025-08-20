package it.univaq.progettotesi.service;

import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository UserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository UserRepository, PasswordEncoder passwordEncoder) {
        this.UserRepository = UserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return UserRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return UserRepository.findById(id);
    }

    public boolean existsByEmail(String email) {
        return UserRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return UserRepository.findByEmail(email);
    }

    public User create(String name, String surname, String email, String password) {
        password = passwordEncoder.encode(password);
        User b = new User(name, surname, email, password, "CLIENT");
        return UserRepository.save(b);
    }

    public User save(User user) {
        return UserRepository.save(user);
    }

    public User update(Long id, String name, String surname) {
        User b = UserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User non trovato: " + id));
        b.setName(name);
        b.setSurname(surname);
        return UserRepository.save(b);
    }

    public void delete(Long id) {
        UserRepository.deleteById(id);
    }
}
