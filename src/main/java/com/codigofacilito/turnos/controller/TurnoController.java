package com.codigofacilito.turnos.controller;

import com.codigofacilito.turnos.dto.TurnoRequest;
import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import com.codigofacilito.turnos.service.TurnoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    // GET /api/turnos                  -> todos
    // GET /api/turnos?estado=PENDIENTE -> filtrados
    @GetMapping
    public List<Turno> listar(@RequestParam(required = false) EstadoTurno estado) {
        return service.listar(estado);
    }

    // GET /api/turnos/1 -> 200 con el turno, o 404
    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtener(@PathVariable Long id) {
        return service.buscar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/turnos -> 201 Created + header Location
    @PostMapping
    public ResponseEntity<Turno> crear(@RequestBody TurnoRequest request) {
        Turno creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    // PUT /api/turnos/1 -> 200 con el turno actualizado, o 404
    @PutMapping("/{id}")
    public ResponseEntity<Turno> actualizar(@PathVariable Long id,
                                            @RequestBody TurnoRequest request) {
        return service.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/turnos/1 -> 204 No Content, o 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
