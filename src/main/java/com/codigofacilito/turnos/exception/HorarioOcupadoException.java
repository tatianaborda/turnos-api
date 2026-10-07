package com.codigofacilito.turnos.exception;

import java.time.LocalDateTime;

public class HorarioOcupadoException extends RuntimeException {

    private final LocalDateTime fechaHora;

    public HorarioOcupadoException(LocalDateTime fechaHora) {
        super("Ya hay un turno reservado para " + fechaHora);
        this.fechaHora = fechaHora;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
