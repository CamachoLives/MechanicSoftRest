package com.mechanicsoft.Features.ConsultaPublica.dto;

import com.mechanicsoft.Features.Clientes.entity.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ConsultaRequest(

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Za-z0-9-]{4,10}$", message = "Placa inválida")
    String placa,

    @NotNull(message = "El tipo de documento es obligatorio")
    TipoDocumento tipoDocumento,

    @NotBlank(message = "El número de documento es obligatorio")
    String numeroDocumento
) {}
