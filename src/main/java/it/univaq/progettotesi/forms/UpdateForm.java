package it.univaq.progettotesi.forms;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UpdateForm(

            @NotBlank(message = "Inserire un nome valido")
            @Size(min = 2, max = 50, message = "Il nome deve avere almeno 2 caratteri, massimo 50")
            String name,

            @NotBlank(message = "Inserire un cognome valido")
            @Size(min = 2, max = 50, message = "Il cognome deve avere almeno 2 caratteri, massimo 50")
            String surname,

            @NotBlank(message = "Inserire una mail valida")
            @Email(message = "Inserire una mail valida")
            String email,



            String oldPassword,



            String newPassword,

            @NotNull(message = "Inserire la data di nascita")
            @Past(message = "La data di nascita deve essere nel passato")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yy")
            LocalDate birthDate,

            @NotBlank(message = "Inserire un numero di telefono")
            // E.164 semplificato: opzionale '+' iniziale, 7–15 cifre
            @Pattern(regexp = "^\\+?[0-9]{7,15}$",
                    message = "Inserire un numero di telefono valido (es. +393331234567)")
            String cellphone
    ) {}

