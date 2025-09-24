package it.univaq.progettotesi.forms;


import jakarta.validation.constraints.*;

public record BuildingForm(

        @NotBlank(message = "Il nome dell'edificio non può essere vuoto")
        String name,

        @NotBlank(message = "È obbligatorio inserire l'indirizzo dell'edificio")
        String address,

        @NotBlank(message = "La classe energetica non può essere vuota")
        String classeEnergetica,

        @NotNull(message = "Il numero di unità abitative è obbligatorio")
        @Min(value = 1, message = "Ci deve essere almeno una unità abitativa")
        Integer unitaAbitative,

        @NotNull(message = "L'anno di costruzione è obbligatorio")
        @Min(value = 1800, message = "L'anno di costruzione non può essere precedente al 1800")
        // Potresti anche aggiungere @Max per l'anno corrente se necessario
        Integer annoCostruzione,

        @NotNull(message = "Il numero di piani è obbligatorio")
        @Min(value = 1, message = "Ci deve essere almeno un piano")
        Integer numeroPiani,

        @NotNull(message = "La superficie è obbligatoria")
        @Positive(message = "La superficie deve essere un valore positivo")
        Double superficie,

        @NotNull(message = "La latitudine è obbligatoria")
        @Min(value = -90, message = "La latitudine deve essere compresa tra -90 e 90")
        @Max(value = 90, message = "La latitudine deve essere compresa tra -90 e 90")
        Double latitudine,

        @NotNull(message = "La longitudine è obbligatoria")
        @Min(value = -180, message = "La longitudine deve essere compresa tra -180 e 180")
        @Max(value = 180, message = "La longitudine deve essere compresa tra -180 e 180")
        Double longitudine
) {}

