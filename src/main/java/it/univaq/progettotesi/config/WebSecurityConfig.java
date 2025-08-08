package it.univaq.progettotesi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login").permitAll()  //Endpoint che non richiedono il login
                .anyRequest().authenticated()) //Tutti gli altri richiedono identificazione
                .formLogin(form -> form
                        .loginPage("/login")        // Reindirizzamento alla pagina login
                        .permitAll())
                .logout(withDefaults());
        return http.build();
    }
}
