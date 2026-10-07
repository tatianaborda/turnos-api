package com.codigofacilito.turnos.exception;

public class TurnoYaCanceladoException extends RuntimeException {

    private final Long id;

    public TurnoYaCanceladoException(Long id) {
        super("El turno " + id + " ya estaba cancelado");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
