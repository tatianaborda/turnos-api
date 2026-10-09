# turnos-api · Clase 3

Proyecto del Módulo 4 del Bootcamp Backend con Java y Spring (Código Facilito).
Estado al final de la **Parte 3 (8/10/2026)**: API validada, documentada con OpenAPI, corriendo sobre Virtual Threads y persistiendo en H2.

## Novedades respecto de la Clase 2
- **Bean Validation:** `@NotBlank`, `@Size`, `@NotNull` y `@Future` en `TurnoRequest` + `@Valid` en el controller. Los errores salen como ProblemDetail 400 con un mapa `errores` por campo.
- **H2 + JdbcClient:** `JdbcTurnoRepository` implementa la misma interfaz que el repositorio en memoria. El controller y el servicio no cambiaron. Tabla en `schema.sql`, datos de ejemplo en `data.sql`.
- **Perfiles:** el repositorio en memoria sigue disponible con el perfil `memoria`.
- **OpenAPI / Swagger UI:** springdoc-openapi 3.1 (la línea compatible con Boot 4.1), con anotaciones `@Tag`, `@Operation`, `@ApiResponse` y `@Schema`.
- **Virtual Threads:** `spring.threads.virtual.enabled=true`. Probá `GET /hola/hilo` con la propiedad en `true` y en `false`.
- **Soluciones de la Clase 2:** `GET /api/calendario/feriados?anio=` (502 si la API externa no responde), fechas pasadas rechazadas (ahora con `@Future`) y caché de feriados por año.

## Ejecutar
```bash
mvn spring-boot:run                                            # H2 (por defecto)
mvn spring-boot:run -Dspring-boot.run.profiles=memoria         # repositorio en memoria
mvn test
```

| Qué | Dónde |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Consola H2 | http://localhost:8080/h2-console (URL `jdbc:h2:mem:turnos`, usuario `sa`, sin password) |

## Native Image (opcional, demo)
Requiere GraalVM 25 instalado (`java -version` debe decir GraalVM).
```bash
mvn -Pnative native:compile     # tarda varios minutos
./target/turnos-api             # arranca en milisegundos
```
Sin GraalVM, con Docker: `mvn -Pnative spring-boot:build-image`.

## Ejercicio para la casa
1. Paginación en `GET /api/turnos`: parámetros `page` y `size` (por defecto 0 y 20, máximo 100) y una respuesta con `contenido`, `pagina`, `tamanio`, `totalElementos` y `totalPaginas`. En Persistencia lo reemplazamos por `Pageable` de Spring Data.
2. Validación propia: una anotación `@HorarioLaboral` que solo acepte turnos de lunes a sábado entre 9 y 19 hs.
3. Abrir Swagger UI y documentar lo que falte para que otra persona pueda consumir la API sin leer el código.

## Cómo sigue
- Persistencia 1 (27/10): JDBC tradicional, JdbcTemplate, JdbcClient, DataSource y HikariCP.
- Persistencia 2 (29/10): Spring Data JPA reemplaza a `JdbcTurnoRepository`.
