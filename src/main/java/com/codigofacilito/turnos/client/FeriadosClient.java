package com.codigofacilito.turnos.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * La versión DECLARATIVA: solo describimos la API remota.
 * Spring genera la implementación (un proxy respaldado por RestClient).
 * Se registra en HttpClientsConfig con @ImportHttpServices.
 */
@HttpExchange("/api/v3")
public interface FeriadosClient {

    @GetExchange("/PublicHolidays/{anio}/{pais}")
    List<Feriado> feriados(@PathVariable int anio, @PathVariable String pais);
}
