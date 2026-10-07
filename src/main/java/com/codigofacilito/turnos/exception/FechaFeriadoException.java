package com.codigofacilito.turnos.exception;

import com.codigofacilito.turnos.client.Feriado;

public class FechaFeriadoException extends RuntimeException {

    private final Feriado feriado;

    public FechaFeriadoException(Feriado feriado) {
        super("No se dan turnos el " + feriado.date() + ": " + feriado.localName());
        this.feriado = feriado;
    }

    public Feriado getFeriado() {
        return feriado;
    }
}
