package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.Turno;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación en memoria. ConcurrentHashMap porque Tomcat atiende
 * cada request en un hilo distinto (buen momento para mencionarlo).
 */
@Repository
public class InMemoryTurnoRepository implements TurnoRepository {

    private final Map<Long, Turno> turnos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public List<Turno> findAll() {
        return new ArrayList<>(turnos.values());
    }

    @Override
    public Optional<Turno> findById(Long id) {
        return Optional.ofNullable(turnos.get(id));
    }

    @Override
    public Turno save(Turno turno) {
        Turno aGuardar = turno.id() == null ? turno.conId(secuencia.incrementAndGet()) : turno;
        turnos.put(aGuardar.id(), aGuardar);
        return aGuardar;
    }

    @Override
    public boolean existsById(Long id) {
        return turnos.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        turnos.remove(id);
    }
}
