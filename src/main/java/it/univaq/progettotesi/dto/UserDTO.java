package it.univaq.progettotesi.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserDTO {
    private Long id;
    private String username;

    // Nuovi campi per il profilo
    private String nome;
    private String password;
    private String cognome;
    private String email;
    private String numeroDiTelefono;
    private Date dataNascita;
    private String ruolo;
    private Long idCondominio;

    // Campo calcolato: numero di condomini gestiti
    private int numeroCondominiGestiti;
}
