package it.univaq.progettotesi;

import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {

        return args -> log.info("Preloading {}",
                repository.save(new User("samuele2003@icloud.com", "ciao", "CLIENT")));
    }
}
