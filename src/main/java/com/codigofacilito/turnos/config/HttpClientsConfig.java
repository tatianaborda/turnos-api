package com.codigofacilito.turnos.config;

import com.codigofacilito.turnos.client.FeriadosClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * Registra FeriadosClient como bean dentro del grupo "feriados".
 * La URL base y los timeouts del grupo se configuran en application.properties:
 *   spring.http.serviceclient.feriados.*
 */
@Configuration
@ImportHttpServices(group = "feriados", types = FeriadosClient.class)
public class HttpClientsConfig {
}
