package com.codigofacilito.turnos.dto;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Versión 2 del contrato: el front pidió fecha y hora por separado.
 * Es un cambio incompatible, por eso va en una versión nueva de la API.
 */
public record TurnoV2Response(
        Long id,
        String cliente,
        String servicio,
        LocalDate fecha,
        LocalTime hora,
        EstadoTurno estado
) {
    public static TurnoV2Response from(Turno turno) {
        return new TurnoV2Response(turno.id(), turno.cliente(), turno.servicio(),
                turno.fechaHora().toLocalDate(), turno.fechaHora().toLocalTime(), turno.estado());
    }
}
