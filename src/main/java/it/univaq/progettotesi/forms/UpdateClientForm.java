package it.univaq.progettotesi.forms;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UpdateClientForm(
        @NotBlank(message = "Inserire un nome valido")
        @Size(min = 2, max = 50)
        String name,

        @NotBlank(message = "Inserire un cognome valido")
        @Size(min = 2, max = 50)
        String surname,

        @NotBlank(message = "Inserire una mail valida")
        @Email
        String email,

        @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
        String password, // opzionale: solo se vuoi cambiare

        @NotNull
        @Past
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate,

        @NotBlank(message = "Inserire un numero di telefono")
        @Pattern(regexp = "^\\+?[0-9]{7,15}$")
        String cellphone
) {
}
