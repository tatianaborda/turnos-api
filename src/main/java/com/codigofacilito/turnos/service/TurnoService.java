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

    /** Filtros opcionales y combinables. */
    public List<Turno> listar(EstadoTurno estado, String cliente) {
        // Si hay estado, la base ya trae solo esos turnos (ahora con una consulta derivada)
        List<Turno> turnos = estado == null ? repository.findAll() : repository.findByEstado(estado);
        return turnos.stream()
                .filter(t -> cliente == null || t.getCliente().toLowerCase().contains(cliente.toLowerCase()))
                .sorted(Comparator.comparing(Turno::getFechaHora))
                .toList();
    }

    /** Ya no devuelve Optional: si no existe, lanza y el handler responde 404. */
    public Turno buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException(id));
    }

    public Turno crear(TurnoRequest request) {
        validarDisponibilidad(request.fechaHora(), null);
        // NUEVO: el constructor de la entidad arranca el turno en PENDIENTE
        Turno nuevo = new Turno(request.cliente(), request.servicio(), request.fechaHora());
        return repository.save(nuevo); // JPA hace el INSERT y le asigna el id
    }

    public Turno actualizar(Long id, TurnoRequest request) {
        Turno actual = buscar(id);
        validarDisponibilidad(request.fechaHora(), id);
        // NUEVO: modificamos la entidad que trajimos y la guardamos (JPA hace el UPDATE)
        actual.actualizar(request.cliente(), request.servicio(), request.fechaHora());
        return repository.save(actual);
    }

    /** Solución del ejercicio 1 de la Parte 1: cancelar dos veces es un conflicto (409). */
    public Turno cancelar(Long id) {
        Turno actual = buscar(id);
        if (actual.getEstado() == EstadoTurno.CANCELADO) {
            throw new TurnoYaCanceladoException(id);
        }
        actual.cancelar();
        return repository.save(actual);
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
                .anyMatch(t -> t.getFechaHora().equals(fechaHora)
                        && t.getEstado() != EstadoTurno.CANCELADO
                        && !t.getId().equals(idPropio));
        if (ocupado) {
            throw new HorarioOcupadoException(fechaHora);
        }
    }
}
