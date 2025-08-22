package it.univaq.progettotesi.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RegisterForm(

        @NotBlank(message = "Inserire un nome valido")
        @Size(min = 2, max = 50, message = "Il nome deve avere almeno 2 caratteri, massimo 50")
        String name,

        @NotBlank(message = "Inserire un cognome valido")
        @Size(min = 2, max = 50, message = "Il cognome deve avere almeno 2 caratteri, massimo 50")
        String surname,

        @NotBlank(message = "Inserire una mail valida")
        @Email(message = "Inserire una mail valida")
        String email,

        @NotBlank(message = "La password non può essere vuota")
        @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
        // @Pattern(
        //   regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        //   message = "La password deve avere almeno 8 caratteri, con maiuscola, minuscola, numero e simbolo"
        // )
        String password
) {}
