package it.univaq.progettotesi.forms;


import jakarta.validation.constraints.NotBlank;

public record BuildingForm(
        @NotBlank(message = "Il nome dell'edificio non può essere vuoto")
        String name,

        @NotBlank(message = "È obbligatorio inserire l'indirizzo dell'edificio")
        String address
        //String ifcGlobalId
) {}

