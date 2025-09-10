package it.univaq.progettotesi.config;

import it.univaq.progettotesi.entity.Admin;
import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final AdminRepository repo;

    public MyUserDetailsService(AdminRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email non trovata:" + email));
        var role = new SimpleGrantedAuthority("ROLE_" + u.getRole().toUpperCase());

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(role)
                .build();
    }


}


