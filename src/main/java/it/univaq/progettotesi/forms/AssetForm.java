package it.univaq.progettotesi.forms;

import it.univaq.progettotesi.entity.AssetType;
import it.univaq.progettotesi.entity.CommProtocol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssetForm(

        @NotBlank(message = "Inserire un nome valido")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Inserire un brand valido")
        @Size(max = 50)
        String brand,


        AssetType type,

        @NotBlank(message = "Inserire un modello valido")
        @Size(max = 100)
        String model,

        
        CommProtocol commProtocol
) {
}
