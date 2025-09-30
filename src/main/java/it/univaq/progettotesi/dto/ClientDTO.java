package it.univaq.progettotesi.dto;

import java.time.LocalDate;

public record ClientDTO(
        Long id,
        String name,
        String surname,
        String email,
        LocalDate birthDate,
        String cellphone
        ) {
}
