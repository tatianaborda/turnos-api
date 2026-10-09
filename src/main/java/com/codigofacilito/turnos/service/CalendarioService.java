package com.codigofacilito.turnos.service;

import com.codigofacilito.turnos.client.Feriado;
import com.codigofacilito.turnos.client.FeriadosClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalendarioService {

    private static final Logger log = LoggerFactory.getLogger(CalendarioService.class);

    private final FeriadosClient feriadosClient;
    private final String pais;

    /**
     * Solución del ejercicio 3 de la Parte 2: los feriados de un año no cambian,
     * así que los guardamos después de la primera consulta.
     * (Más adelante esto se reemplaza por @Cacheable y un proveedor de caché.)
     */
    private final Map<Integer, List<Feriado>> cache = new ConcurrentHashMap<>();

    public CalendarioService(FeriadosClient feriadosClient, @Value("${turnos.pais}") String pais) {
        this.feriadosClient = feriadosClient;
        this.pais = pais;
    }

    /** Si la API externa falla, lanza RestClientException (el handler responde 502). */
    public List<Feriado> feriadosDe(int anio) {
        return cache.computeIfAbsent(anio, a -> feriadosClient.feriados(a, pais));
    }

    /** Fail-open: si la API externa no responde, se permite reservar igual. */
    public Optional<Feriado> feriadoEn(LocalDate fecha) {
        try {
            return feriadosDe(fecha.getYear()).stream()
                    .filter(f -> f.date().equals(fecha))
                    .findFirst();
        } catch (RestClientException ex) {
            log.warn("No se pudo consultar la API de feriados: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
