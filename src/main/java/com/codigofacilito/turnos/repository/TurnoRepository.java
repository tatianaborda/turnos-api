package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * NUEVO (P2-LC1): el repositorio es solo una interfaz. No hay clase que la implemente:
 * Spring Data JPA la genera al arrancar.
 *
 * JpaRepository<Turno, Long> = repositorio de Turno, cuya clave primaria es Long.
 * Ya trae: findAll, findById, save, existsById, deleteById, count y muchos más.
 * (Son los mismos nombres que usábamos desde la clase 1: por eso el servicio casi no cambia.)
 */
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // Consulta derivada: Spring arma el SQL a partir del nombre del método.
    // findBy + Estado  ->  SELECT ... FROM turno WHERE estado = ?
    List<Turno> findByEstado(EstadoTurno estado);
}
