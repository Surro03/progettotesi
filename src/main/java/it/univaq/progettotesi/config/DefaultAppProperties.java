package it.univaq.progettotesi.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.login")

public class DefaultAppProperties {

    @Getter
    @Setter
    String username;

    @Getter
    @Setter
    String password;

}
