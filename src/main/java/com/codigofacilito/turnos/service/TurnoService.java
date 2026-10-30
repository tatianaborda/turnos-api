package com.codigofacilito.turnos.service;

import com.codigofacilito.turnos.dto.TurnoRequest;
import com.codigofacilito.turnos.exception.FechaFeriadoException;
import com.codigofacilito.turnos.exception.HorarioOcupadoException;
import com.codigofacilito.turnos.exception.TurnoNoEncontradoException;
import com.codigofacilito.turnos.exception.TurnoYaCanceladoException;
import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import com.codigofacilito.turnos.repository.TurnoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository repository;
    private final CalendarioService calendario;

    public TurnoService(TurnoRepository repository, CalendarioService calendario) {
        this.repository = repository;
        this.calendario = calendario;
    }

    // NUEVO: el orden también lo resuelve la base (ORDER BY fecha_hora)
    private static final Sort POR_FECHA = Sort.by("fechaHora");

    /**
     * Filtros opcionales y combinables.
     * NUEVO: ya no traemos todo para filtrar en Java: cada combinación
     * de filtros usa su consulta derivada, y la base devuelve solo lo que hace falta.
     */
    public List<Turno> listar(EstadoTurno estado, String cliente) {
        if (estado != null && cliente != null) {
            return repository.findByEstadoAndClienteContainingIgnoreCase(estado, cliente, POR_FECHA);
        }
        if (estado != null) {
            return repository.findByEstado(estado, POR_FECHA);
        }
        if (cliente != null) {
            return repository.findByClienteContainingIgnoreCase(cliente, POR_FECHA);
        }
        return repository.findAll(POR_FECHA);
    }

    /** NUEVO: la agenda de un día, con la consulta @Query del repositorio. */
    public List<Turno> agenda(LocalDate dia) {
        return repository.agendaEntre(dia.atStartOfDay(), dia.plusDays(1).atStartOfDay());
    }

    /** Ya no devuelve Optional: si no existe, lanza y el handler responde 404. */
    public Turno buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException(id));
    }

    public Turno crear(TurnoRequest request) {
        validarDisponibilidad(request.fechaHora(), null);
        // NUEVO : el constructor de la entidad arranca el turno en PENDIENTE
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
        // NUEVO: antes traíamos TODOS los turnos para revisar en Java.
        // Ahora le preguntamos a la base con una consulta que devuelve true o false.
        boolean ocupado = idPropio == null
                ? repository.existsByFechaHoraAndEstadoNot(fechaHora, EstadoTurno.CANCELADO)
                : repository.existsByFechaHoraAndEstadoNotAndIdNot(fechaHora, EstadoTurno.CANCELADO, idPropio);
        if (ocupado) {
            throw new HorarioOcupadoException(fechaHora);
        }
    }
}
