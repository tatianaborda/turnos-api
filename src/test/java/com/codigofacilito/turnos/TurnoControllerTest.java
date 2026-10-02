package com.codigofacilito.turnos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de humo para que los alumnos vean que la API responde.
 * El testing en profundidad es el Módulo 9.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TurnoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void crearTurnoDevuelve201ConLocation() throws Exception {
        String body = """
                {"cliente":"Ana Pérez","servicio":"Corte de pelo","fechaHora":"2026-10-05T10:30:00"}
                """;

        mockMvc.perform(post("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void turnoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/turnos/9999"))
                .andExpect(status().isNotFound());
    }
}
