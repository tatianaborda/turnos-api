package com.codigofacilito.turnos.client;

import java.time.LocalDate;

/**
 * Solo mapeamos los campos que nos interesan de la respuesta de Nager.Date.
 * Spring Boot configura Jackson para ignorar los campos desconocidos.
 */
public record Feriado(
        LocalDate date,
        String localName,
        String name
) {
}
