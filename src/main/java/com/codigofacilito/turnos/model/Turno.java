package com.codigofacilito.turnos.model;

import java.time.LocalDateTime;

public record Turno(
        Long id,
        String cliente,
        String servicio,
        LocalDateTime fechaHora,
        EstadoTurno estado
) {
    public Turno conId(Long nuevoId) {
        return new Turno(nuevoId, cliente, servicio, fechaHora, estado);
    }

    public Turno conEstado(EstadoTurno nuevoEstado) {
        return new Turno(id, cliente, servicio, fechaHora, nuevoEstado);
    }
}
