package com.codigofacilito.turnos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Lo que el cliente envía en POST y PUT.
 * Las anotaciones validan el FORMATO del request (400).
 * Las reglas de NEGOCIO (feriado, horario ocupado) siguen en el servicio (422 / 409).
 */
public record TurnoRequest(

        @Schema(example = "Ana Pérez")
        @NotBlank(message = "El cliente es obligatorio")
        @Size(max = 100, message = "El cliente no puede superar los 100 caracteres")
        String cliente,

        @Schema(example = "Corte de pelo")
        @NotBlank(message = "El servicio es obligatorio")
        @Size(max = 100, message = "El servicio no puede superar los 100 caracteres")
        String servicio,

        @Schema(example = "2030-03-10T10:30:00")
        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "El turno tiene que ser en el futuro")
        LocalDateTime fechaHora
) {
}
