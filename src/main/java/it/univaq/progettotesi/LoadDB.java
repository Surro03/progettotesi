package it.univaq.progettotesi;

import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.repository.UserRepository;
import it.univaq.progettotesi.repository.BuildingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, BuildingRepository buildingRepo) {
        return args -> {
            User u = new User("Samuele", "Surricchio", "samuele2003@icloud.com", "{noop}ciao", "CLIENT");
            u = userRepo.save(u);
            log.info("Preloaded user: {}", u.getEmail());

            Building b = new Building(u, "Cinque", "Via Cervaro 15");
            b = buildingRepo.save(b);
            log.info("Preloaded building: {} (owner: {})", b.getName(), u.getEmail());
        };
    }
}
