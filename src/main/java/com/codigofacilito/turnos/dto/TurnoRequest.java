package com.codigofacilito.turnos.dto;

import java.time.LocalDateTime;

/**
 * Lo que el cliente envía en POST y PUT.
 * No incluye id ni estado: esos los decide el servidor.
 * (La validación con Bean Validation la agregamos en la Parte 3.)
 */
public record TurnoRequest(
        String cliente,
        String servicio,
        LocalDateTime fechaHora
) {
}
