package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * El repositorio es solo una interfaz: Spring Data JPA genera la implementación.
 * JpaRepository<Turno, Long> ya trae findAll, findById, save, existsById, deleteById, count...
 */
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // ---------- NUEVO (P2-LC2): consultas derivadas del nombre del método ----------

    // WHERE estado = ?   (el Sort agrega el ORDER BY)
    List<Turno> findByEstado(EstadoTurno estado, Sort orden);

    // WHERE UPPER(cliente) LIKE UPPER('%' || ? || '%')
    List<Turno> findByClienteContainingIgnoreCase(String cliente, Sort orden);

    // WHERE estado = ? AND UPPER(cliente) LIKE UPPER('%' || ? || '%')
    List<Turno> findByEstadoAndClienteContainingIgnoreCase(EstadoTurno estado, String cliente, Sort orden);

    // ¿Existe un turno en ese horario que no esté cancelado?  (para crear)
    boolean existsByFechaHoraAndEstadoNot(LocalDateTime fechaHora, EstadoTurno estado);

    // Lo mismo, sin contar al propio turno  (para actualizar)
    boolean existsByFechaHoraAndEstadoNotAndIdNot(LocalDateTime fechaHora, EstadoTurno estado, Long id);

    // ---------- NUEVO (P2-LC3): las mismas consultas, paginadas ----------
    // Con Pageable, Spring agrega LIMIT/OFFSET y ORDER BY, y además hace un COUNT
    // para saber el total de elementos. (findAll(Pageable) ya viene en JpaRepository.)

    Page<Turno> findByEstado(EstadoTurno estado, Pageable pageable);

    Page<Turno> findByClienteContainingIgnoreCase(String cliente, Pageable pageable);

    Page<Turno> findByEstadoAndClienteContainingIgnoreCase(EstadoTurno estado, String cliente, Pageable pageable);

    // ---------- NUEVO (P2-LC2): consulta escrita a mano con JPQL ----------
    // JPQL se parece a SQL, pero habla de entidades y atributos (Turno, fechaHora),
    // no de tablas y columnas (turno, fecha_hora).
    @Query("""
            SELECT t FROM Turno t
            WHERE t.fechaHora >= :desde AND t.fechaHora < :hasta
              AND t.estado <> com.codigofacilito.turnos.model.EstadoTurno.CANCELADO
            ORDER BY t.fechaHora
            """)
    List<Turno> agendaEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
