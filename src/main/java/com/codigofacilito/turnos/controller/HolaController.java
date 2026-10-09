package com.codigofacilito.turnos.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Hidden // no aparece en Swagger
@RestController
public class HolaController {

    @GetMapping("/hola")
    public Map<String, String> hola(@RequestParam(defaultValue = "mundo") String nombre) {
        return Map.of("mensaje", "¡Hola, " + nombre + "!");
    }

    /**
     * Parte 3: ¿en qué hilo se ejecuta este request?
     * Probalo con spring.threads.virtual.enabled en true y en false.
     */
    @GetMapping("/hola/hilo")
    public Map<String, Object> hilo() {
        Thread actual = Thread.currentThread();
        return Map.of(
                "hilo", actual.toString(),
                "virtual", actual.isVirtual());
    }
}
