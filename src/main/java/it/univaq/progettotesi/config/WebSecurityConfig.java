package it.univaq.progettotesi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/css/**", "/javascript/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/register").permitAll()  //Endpoint che non richiedono il login
                        .anyRequest().authenticated()) //Tutti gli altri richiedono identificazione
                        .formLogin(form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticate") // URL che Spring usa per il POST
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/home", true)    // redirect dopo login
                                .failureUrl("/login?error=true")     // redirect in caso di errore
                        )
                        .logout(logout -> logout
                                .logoutSuccessUrl("/login?logout=true")
                                .permitAll()
                        );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.factory.PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }


}
