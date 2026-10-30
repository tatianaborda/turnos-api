~~# turnos-api · Persistencia, Parte 2

Proyecto del Bootcamp Backend con Java y Spring (Código Facilito).
Estado al final de **Persistencia de Datos · Parte 2 (29/10/2026)**: la API accede a la base con **Spring Data JPA**.

## Novedades respecto de Persistencia 1
- **Solución del ejercicio de Persistencia 1:** `findByEstado` con un `WHERE` en el SQL.
- **Entidad JPA (LC1):** `Turno` deja de ser un record y pasa a ser una clase con `@Entity`, `@Id`, `@GeneratedValue`, `@Column` y `@Enumerated`.
- **JpaRepository (LC1):** `TurnoRepository` es solo una interfaz; Spring Data genera la implementación. Se borró `JdbcTemplateTurnoRepository`.
- **Consultas (LC2):** consultas derivadas del nombre del método (`findByEstado`, `findByClienteContainingIgnoreCase`, `existsByFechaHoraAndEstadoNot`...), ordenamiento con `Sort` y una consulta JPQL con `@Query` para la agenda del día.
- **Paginación (LC3):** `GET /api/turnos` con `API-Version: 3.0` devuelve una página (`page`, `size`, `sort`).

## Ejecutar
```bash
mvn spring-boot:run
mvn test        # los tests usan una base en memoria
```

## Endpoints nuevos
| Método | URI | Qué hace |
|---|---|---|
| GET | `/api/turnos/agenda?fecha=2030-03-04` | Turnos no cancelados de un día, ordenados por hora |
| GET | `/api/turnos?page=0&size=2&sort=fechaHora,desc` + header `API-Version: 3.0` | Listado paginado |

En la consola de IntelliJ se ve el SQL que genera Hibernate (`spring.jpa.show-sql=true`).
