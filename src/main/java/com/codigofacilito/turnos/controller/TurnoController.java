package com.codigofacilito.turnos.controller;

import com.codigofacilito.turnos.dto.TurnoRequest;
import com.codigofacilito.turnos.dto.TurnoV2Response;
import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import com.codigofacilito.turnos.service.TurnoService;
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
import java.util.List;

/**
 * Cambios respecto de la Parte 1:
 *  - Sin Optional ni notFound(): los errores los resuelve GlobalExceptionHandler.
 *  - Los GET tienen versión 1.0 y 2.0 (header API-Version).
 *    Los endpoints sin "version" responden a cualquier versión.
 */
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    // ---------- Versión 1.0 (por defecto) ----------

    @GetMapping(version = "1.0")
    public List<Turno> listarV1(@RequestParam(required = false) EstadoTurno estado,
                                @RequestParam(required = false) String cliente) {
        return service.listar(estado, cliente);
    }

    @GetMapping(path = "/{id}", version = "1.0")
    public Turno obtenerV1(@PathVariable Long id) {
        return service.buscar(id);
    }

    // ---------- Versión 2.0: fecha y hora separadas ----------

    @GetMapping(version = "2.0")
    public List<TurnoV2Response> listarV2(@RequestParam(required = false) EstadoTurno estado,
                                         @RequestParam(required = false) String cliente) {
        return service.listar(estado, cliente).stream().map(TurnoV2Response::from).toList();
    }

    @GetMapping(path = "/{id}", version = "2.0")
    public TurnoV2Response obtenerV2(@PathVariable Long id) {
        return TurnoV2Response.from(service.buscar(id));
    }

    // ---------- Sin versión: aplican a todas ----------

    @PostMapping
    public ResponseEntity<Turno> crear(@RequestBody TurnoRequest request) {
        Turno creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public Turno actualizar(@PathVariable Long id, @RequestBody TurnoRequest request) {
        return service.actualizar(id, request);
    }

    @PatchMapping("/{id}/cancelar")
    public Turno cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
