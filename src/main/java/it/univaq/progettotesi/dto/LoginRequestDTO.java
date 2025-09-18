package it.univaq.progettotesi.dto;


import lombok.Data;


public record LoginRequestDTO(
        String username,
        String password
){
}
