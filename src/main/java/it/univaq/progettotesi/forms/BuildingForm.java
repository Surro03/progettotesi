package it.univaq.progettotesi.forms;


import jakarta.validation.constraints.NotBlank;

public record BuildingForm(
        @NotBlank String name,
        @NotBlank String address
        //String ifcGlobalId
) {}

