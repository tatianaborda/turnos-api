package com.codigofacilito.turnos.dto;

import java.time.LocalDateTime;

/**
 * Lo que el cliente envía en POST y PUT.
 * No incluye id ni estado: esos los decide el servidor.
 */
public record TurnoRequest(
        String cliente,
        String servicio,
        LocalDateTime fechaHora
) {
}
