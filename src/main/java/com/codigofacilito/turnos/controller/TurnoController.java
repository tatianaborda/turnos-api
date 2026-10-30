package com.codigofacilito.turnos.controller;

import com.codigofacilito.turnos.dto.PaginaResponse;
import com.codigofacilito.turnos.dto.TurnoRequest;
import com.codigofacilito.turnos.dto.TurnoV2Response;
import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import com.codigofacilito.turnos.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * Cambios respecto de la Parte 2:
 *  - @Valid en los @RequestBody: los errores de formato salen como ProblemDetail 400.
 *  - NUEVO (LC2): anotaciones de OpenAPI para enriquecer la documentación de Swagger UI.
 */
@Tag(name = "Turnos", description = "Reserva y gestión de turnos")
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    // ---------- Versión 1.0 (por defecto) ----------

    @Operation(summary = "Lista turnos (v1)", description = "Filtros opcionales y combinables por estado y cliente")
    @GetMapping(version = "1.0")
    public List<Turno> listarV1(@RequestParam(required = false) EstadoTurno estado,
                                @RequestParam(required = false) String cliente) {
        return service.listar(estado, cliente);
    }

    @Operation(summary = "Obtiene un turno (v1)")
    @ApiResponse(responseCode = "200", description = "Turno encontrado")
    @ApiResponse(responseCode = "404", description = "No existe el turno",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(path = "/{id}", version = "1.0")
    public Turno obtenerV1(@PathVariable Long id) {
        return service.buscar(id);
    }

    // ---------- Versión 2.0: fecha y hora separadas ----------

    @Operation(summary = "Lista turnos (v2)", description = "Devuelve fecha y hora por separado")
    @GetMapping(version = "2.0")
    public List<TurnoV2Response> listarV2(@RequestParam(required = false) EstadoTurno estado,
                                         @RequestParam(required = false) String cliente) {
        return service.listar(estado, cliente).stream().map(TurnoV2Response::from).toList();
    }

    @Operation(summary = "Obtiene un turno (v2)")
    @GetMapping(path = "/{id}", version = "2.0")
    public TurnoV2Response obtenerV2(@PathVariable Long id) {
        return TurnoV2Response.from(service.buscar(id));
    }

    // ---------- NUEVO (P2-LC3): versión 3.0, listado paginado ----------
    // Paginar cambia la forma de la respuesta (de una lista a una página con datos extra),
    // así que lo publicamos como versión nueva: los clientes de la 1.0 y la 2.0 no se rompen.
    // Ejemplo: GET /api/turnos?page=0&size=2&sort=fechaHora,desc  con header API-Version: 3.0

    @Operation(summary = "Lista turnos paginados (v3)", description = "Parámetros: page (desde 0), size y sort=campo,asc|desc")
    @GetMapping(version = "3.0")
    public PaginaResponse<TurnoV2Response> listarV3(
            @RequestParam(required = false) EstadoTurno estado,
            @RequestParam(required = false) String cliente,
            @ParameterObject @PageableDefault(size = 20, sort = "fechaHora") Pageable pageable) {
        return PaginaResponse.from(service.listarPaginado(estado, cliente, pageable), TurnoV2Response::from);
    }

    // ---------- NUEVO (P2-LC2): agenda de un día (consulta @Query) ----------

    @Operation(summary = "Agenda de un día", description = "Turnos no cancelados de la fecha, ordenados por hora")
    @GetMapping("/agenda")
    public List<Turno> agenda(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.agenda(fecha);
    }

    // ---------- Sin versión: aplican a todas ----------

    @Operation(summary = "Reserva un turno nuevo")
    @ApiResponse(responseCode = "201", description = "Turno creado; el header Location apunta al recurso")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Horario ocupado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "La fecha es feriado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<Turno> crear(@Valid @RequestBody TurnoRequest request) {
        Turno creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @Operation(summary = "Reemplaza los datos de un turno")
    @PutMapping("/{id}")
    public Turno actualizar(@PathVariable Long id, @Valid @RequestBody TurnoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Cancela un turno", description = "409 si ya estaba cancelado")
    @PatchMapping("/{id}/cancelar")
    public Turno cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @Operation(summary = "Elimina un turno")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
