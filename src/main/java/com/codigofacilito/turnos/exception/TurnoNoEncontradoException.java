package com.codigofacilito.turnos.exception;

public class TurnoNoEncontradoException extends RuntimeException {

    private final Long id;

    public TurnoNoEncontradoException(Long id) {
        super("No existe un turno con id " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
