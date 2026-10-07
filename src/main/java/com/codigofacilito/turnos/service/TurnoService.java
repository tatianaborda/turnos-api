package com.codigofacilito.turnos.service;

import com.codigofacilito.turnos.dto.TurnoRequest;
import com.codigofacilito.turnos.exception.FechaFeriadoException;
import com.codigofacilito.turnos.exception.HorarioOcupadoException;
import com.codigofacilito.turnos.exception.TurnoNoEncontradoException;
import com.codigofacilito.turnos.exception.TurnoYaCanceladoException;
import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import com.codigofacilito.turnos.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository repository;
    private final CalendarioService calendario;

    public TurnoService(TurnoRepository repository, CalendarioService calendario) {
        this.repository = repository;
        this.calendario = calendario;
    }

    /** Filtros opcionales y combinables (solución del ejercicio 2 de la Parte 1). */
    public List<Turno> listar(EstadoTurno estado, String cliente) {
        return repository.findAll().stream()
                .filter(t -> estado == null || t.estado() == estado)
                .filter(t -> cliente == null || t.cliente().toLowerCase().contains(cliente.toLowerCase()))
                .sorted(Comparator.comparing(Turno::fechaHora))
                .toList();
    }

    /** Ya no devuelve Optional: si no existe, lanza y el handler responde 404. */
    public Turno buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException(id));
    }

    public Turno crear(TurnoRequest request) {
        validarDisponibilidad(request.fechaHora(), null);
        Turno nuevo = new Turno(null, request.cliente(), request.servicio(),
                request.fechaHora(), EstadoTurno.PENDIENTE);
        return repository.save(nuevo);
    }

    public Turno actualizar(Long id, TurnoRequest request) {
        Turno actual = buscar(id);
        validarDisponibilidad(request.fechaHora(), id);
        return repository.save(new Turno(id, request.cliente(), request.servicio(),
                request.fechaHora(), actual.estado()));
    }

    /** Solución del ejercicio 1 de la Parte 1: cancelar dos veces es un conflicto (409). */
    public Turno cancelar(Long id) {
        Turno actual = buscar(id);
        if (actual.estado() == EstadoTurno.CANCELADO) {
            throw new TurnoYaCanceladoException(id);
        }
        return repository.save(actual.conEstado(EstadoTurno.CANCELADO));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new TurnoNoEncontradoException(id);
        }
        repository.deleteById(id);
    }

    private void validarDisponibilidad(LocalDateTime fechaHora, Long idPropio) {
        calendario.feriadoEn(fechaHora.toLocalDate()).ifPresent(feriado -> {
            throw new FechaFeriadoException(feriado);
        });
        boolean ocupado = repository.findAll().stream()
                .anyMatch(t -> t.fechaHora().equals(fechaHora)
                        && t.estado() != EstadoTurno.CANCELADO
                        && !t.id().equals(idPropio));
        if (ocupado) {
            throw new HorarioOcupadoException(fechaHora);
        }
    }
}
