package com.codigofacilito.turnos.service;

import com.codigofacilito.turnos.client.Feriado;
import com.codigofacilito.turnos.client.FeriadosClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CalendarioService {

    private static final Logger log = LoggerFactory.getLogger(CalendarioService.class);

    private final FeriadosClient feriadosClient;
    private final String pais;

    public CalendarioService(FeriadosClient feriadosClient, @Value("${turnos.pais}") String pais) {
        this.feriadosClient = feriadosClient;
        this.pais = pais;
    }

    /**
     * Devuelve el feriado de esa fecha, si lo hay.
     * Decisión de diseño "fail-open": si la API externa no responde,
     * dejamos reservar igual en vez de tirar abajo nuestro servicio.
     * (En Spring Cloud vemos circuit breakers para esto.)
     */
    public Optional<Feriado> feriadoEn(LocalDate fecha) {
        try {
            return feriadosClient.feriados(fecha.getYear(), pais).stream()
                    .filter(f -> f.date().equals(fecha))
                    .findFirst();
        } catch (RestClientException ex) {
            log.warn("No se pudo consultar la API de feriados: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
