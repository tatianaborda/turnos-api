# turnos-api · Clase 1

Proyecto que acompaña el Módulo 4 del Bootcamp Backend con Java y Spring (Código Facilito).
Estado al final de la **Parte 1 (1/10/2026)**: CRUD de turnos en memoria con Spring Web.

## Requisitos
- Java 21 o superior (Java 25 también funciona)
- Maven 3.9+ (o generá el wrapper con `mvn wrapper:wrapper`)
- Spring Boot 4.1.x (si hay un patch más nuevo en start.spring.io, actualizá la versión del `pom.xml`)

## Ejecutar
```bash
mvn spring-boot:run            # modo desarrollo
mvn clean package              # genera el fat JAR en target/
java -jar target/turnos-api-0.1.0.jar
java -jar target/turnos-api-0.1.0.jar --server.port=9090   # override de propiedad
```

## Endpoints
| Método | URI | Respuesta |
|---|---|---|
| GET | `/hola?nombre=X` | 200 |
| GET | `/api/turnos` (`?estado=PENDIENTE` opcional) | 200 |
| GET | `/api/turnos/{id}` | 200 / 404 |
| POST | `/api/turnos` | 201 + `Location` |
| PUT | `/api/turnos/{id}` | 200 / 404 |
| DELETE | `/api/turnos/{id}` | 204 / 404 |

Para probar: `http/turnos.http` (IntelliJ o VS Code REST Client) o `http/curl.sh`.

## Estructura
```
controller/  HolaController, TurnoController   → capa HTTP
service/     TurnoService                      → reglas de negocio
repository/  TurnoRepository (interfaz)        → acceso a datos
             InMemoryTurnoRepository           → hoy: ConcurrentHashMap
model/       Turno (record), EstadoTurno (enum)
dto/         TurnoRequest (record)             → lo que envía el cliente
```

## Ejercicio para la casa
1. `PATCH /api/turnos/{id}/cancelar` → pasa el turno a `CANCELADO` (pista: `Turno.conEstado`). Devuelve 200, o 404 si no existe.
2. `GET /api/turnos?cliente=Ana` → filtro por cliente (combinable con `estado`).
3. Pensar: ¿qué status debería devolver cancelar un turno que ya está cancelado? Lo discutimos en la Parte 2.

## Cómo sigue
- Parte 2: `RestClient`, `@HttpExchange`, errores con `ProblemDetail`, versionado de APIs.
- Parte 3: validación, buenas prácticas, Virtual Threads, OpenAPI/Swagger y H2.
- Persistencia: el `InMemoryTurnoRepository` se reemplaza por JdbcTemplate y Spring Data JPA.
