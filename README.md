# turnos-api · Clase 2

Proyecto del Módulo 4 del Bootcamp Backend con Java y Spring (Código Facilito).
Estado al final de la **Parte 2 (6/10/2026)**: la API consulta feriados en una API externa, devuelve errores estandarizados con ProblemDetail y tiene dos versiones de sus endpoints de lectura.

## Novedades respecto de la Clase 1
- **Consumo de APIs:** `FeriadosRestClient` (RestClient imperativo, para comparar) y `FeriadosClient` (interfaz `@HttpExchange`, la que usa el servicio). Fuente: [Nager.Date](https://date.nager.at), gratuita y sin API key.
- **Nuevo starter:** `spring-boot-starter-restclient` (en Boot 4 ya no viene con el de Web MVC).
- **Reglas de negocio:** no se reservan turnos en feriados (422) ni en horarios ocupados (409).
- **Errores RFC 9457:** `GlobalExceptionHandler` devuelve `application/problem+json` con propiedades propias.
- **Versionado:** header `API-Version`. `1.0` (por defecto) devuelve `fechaHora`; `2.0` devuelve `fecha` y `hora` por separado.
- **Solución del ejercicio de la Clase 1:** `PATCH /api/turnos/{id}/cancelar` (409 si ya estaba cancelado) y filtro `?cliente=`.

## Ejecutar
```bash
mvn spring-boot:run
mvn test        # FeriadosClient se mockea con @MockitoBean: no necesita internet
```
Cambiá el país de los feriados en `application.properties` (`turnos.pais=AR`, `BR`, `MX`, `CO`...).

## Endpoints
| Método | URI | Éxito | Errores |
|---|---|---|---|
| GET | `/api/turnos` (`?estado=`, `?cliente=`) | 200 | 400 versión inválida |
| GET | `/api/turnos/{id}` | 200 | 404 |
| POST | `/api/turnos` | 201 | 400, 409, 422 |
| PUT | `/api/turnos/{id}` | 200 | 404, 409, 422 |
| PATCH | `/api/turnos/{id}/cancelar` | 200 | 404, 409 |
| DELETE | `/api/turnos/{id}` | 204 | 404 |

## Ejercicio para la casa
1. `GET /api/calendario/feriados?anio=2026` que exponga los feriados usando `FeriadosClient` (nuevo controller, sin tocar `TurnoController`).
2. Rechazar turnos en fechas pasadas con un ProblemDetail 422 que incluya la propiedad `fechaHora`.
3. Para pensar: hoy cada `POST` consulta la API de feriados. ¿Cómo lo evitarías? (Pista: los feriados de un año no cambian.)

## Cómo sigue
- Parte 3 (8/10): Bean Validation, buenas prácticas de diseño, Virtual Threads, AOT, Spring Modulith, OpenAPI/Swagger y H2.
