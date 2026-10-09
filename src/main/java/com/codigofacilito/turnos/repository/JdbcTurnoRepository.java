package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Misma interfaz, otra implementación: el controller y el servicio no cambian.
 * JdbcClient (Spring 6.1+) es la API fluida sobre JDBC.
 * En el módulo de Persistencia lo comparamos con JDBC tradicional,
 * JdbcTemplate y Spring Data JPA.
 */
@Repository
public class JdbcTurnoRepository implements TurnoRepository {

    private static final String COLUMNAS = "id, cliente, servicio, fecha_hora, estado";

    private final JdbcClient jdbc;

    public JdbcTurnoRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Turno> findAll() {
        return jdbc.sql("SELECT " + COLUMNAS + " FROM turno")
                .query(this::mapear)
                .list();
    }

    @Override
    public Optional<Turno> findById(Long id) {
        return jdbc.sql("SELECT " + COLUMNAS + " FROM turno WHERE id = :id")
                .param("id", id)
                .query(this::mapear)
                .optional();
    }

    @Override
    public Turno save(Turno turno) {
        if (turno.id() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.sql("""
                    INSERT INTO turno (cliente, servicio, fecha_hora, estado)
                    VALUES (:cliente, :servicio, :fechaHora, :estado)
                    """)
                    .param("cliente", turno.cliente())
                    .param("servicio", turno.servicio())
                    .param("fechaHora", turno.fechaHora())
                    .param("estado", turno.estado().name())
                    .update(keyHolder);
            return turno.conId(keyHolder.getKey().longValue());
        }
        jdbc.sql("""
                UPDATE turno
                   SET cliente = :cliente, servicio = :servicio,
                       fecha_hora = :fechaHora, estado = :estado
                 WHERE id = :id
                """)
                .param("cliente", turno.cliente())
                .param("servicio", turno.servicio())
                .param("fechaHora", turno.fechaHora())
                .param("estado", turno.estado().name())
                .param("id", turno.id())
                .update();
        return turno;
    }

    @Override
    public boolean existsById(Long id) {
        Integer cantidad = jdbc.sql("SELECT COUNT(*) FROM turno WHERE id = :id")
                .param("id", id)
                .query(Integer.class)
                .single();
        return cantidad > 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.sql("DELETE FROM turno WHERE id = :id")
                .param("id", id)
                .update();
    }

    private Turno mapear(ResultSet rs, int fila) throws SQLException {
        return new Turno(
                rs.getLong("id"),
                rs.getString("cliente"),
                rs.getString("servicio"),
                rs.getObject("fecha_hora", LocalDateTime.class),
                EstadoTurno.valueOf(rs.getString("estado")));
    }
}
