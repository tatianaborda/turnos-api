package com.codigofacilito.turnos.service;

import com.codigofacilito.turnos.dto.TurnoRequest;
import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import com.codigofacilito.turnos.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoService {

    private final TurnoRepository repository;

    // Inyección por constructor: no hace falta @Autowired
    public TurnoService(TurnoRepository repository) {
        this.repository = repository;
    }

    public List<Turno> listar(EstadoTurno estado) {
        return repository.findAll().stream()
                .filter(t -> estado == null || t.estado() == estado)
                .sorted(Comparator.comparing(Turno::fechaHora))
                .toList();
    }

    public Optional<Turno> buscar(Long id) {
        return repository.findById(id);
    }

    public Turno crear(TurnoRequest request) {
        Turno nuevo = new Turno(null, request.cliente(), request.servicio(),
                request.fechaHora(), EstadoTurno.PENDIENTE);
        return repository.save(nuevo);
    }

    /** PUT: reemplaza los datos del turno, conservando id y estado. */
    public Optional<Turno> actualizar(Long id, TurnoRequest request) {
        return repository.findById(id)
                .map(actual -> repository.save(new Turno(id, request.cliente(),
                        request.servicio(), request.fechaHora(), actual.estado())));
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
