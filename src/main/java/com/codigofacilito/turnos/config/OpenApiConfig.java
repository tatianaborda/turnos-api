package com.codigofacilito.turnos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "turnos-api",
        version = "0.3.0",
        description = "API de reserva de turnos · Bootcamp Backend con Java y Spring (Código Facilito)",
        contact = @Contact(name = "Bootcamp Backend", url = "https://codigofacilito.com")))
public class OpenApiConfig {
}
