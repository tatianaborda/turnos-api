package com.codigofacilito.turnos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Live coding 1: la versión IMPERATIVA con RestClient.
 * La dejamos en el proyecto para comparar con la versión declarativa
 * (FeriadosClient). El servicio usa la declarativa.
 */
@Component
public class FeriadosRestClient {

    private final RestClient restClient;

    public FeriadosRestClient(RestClient.Builder builder,
                              @Value("${spring.http.serviceclient.feriados.base-url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "turnos-api")
                .build();
    }

    public List<Feriado> feriados(int anio, String pais) {
        return restClient.get()
                .uri("/api/v3/PublicHolidays/{anio}/{pais}", anio, pais)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new IllegalArgumentException("País no soportado: " + pais);
                })
                .body(new ParameterizedTypeReference<List<Feriado>>() {});
    }
}
