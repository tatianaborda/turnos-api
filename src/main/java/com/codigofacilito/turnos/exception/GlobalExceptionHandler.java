package com.codigofacilito.turnos.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce excepciones a respuestas RFC 9457 (application/problem+json).
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String BASE_TYPE = "https://turnos-api.dev/errores/";

    // ---------- NUEVO (LC1): errores de Bean Validation ----------

    /**
     * Por defecto Spring responde 400 con un detail genérico.
     * Lo sobrescribimos para devolver qué campo falló y por qué.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(),
                    error.getDefaultMessage() != null ? error.getDefaultMessage() : "Valor inválido");
        }
        ProblemDetail problem = ex.getBody();
        problem.setTitle("Datos inválidos");
        problem.setDetail("El request tiene " + errores.size() + " campo(s) con errores");
        problem.setType(URI.create(BASE_TYPE + "datos-invalidos"));
        problem.setProperty("errores", errores);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    // ---------- Parte 2: excepciones de dominio ----------

    @ExceptionHandler(TurnoNoEncontradoException.class)
    public ProblemDetail turnoNoEncontrado(TurnoNoEncontradoException ex) {
        ProblemDetail problem = problema(HttpStatus.NOT_FOUND, "Turno no encontrado", "turno-no-encontrado", ex);
        problem.setProperty("turnoId", ex.getId());
        return problem;
    }

    @ExceptionHandler(HorarioOcupadoException.class)
    public ProblemDetail horarioOcupado(HorarioOcupadoException ex) {
        ProblemDetail problem = problema(HttpStatus.CONFLICT, "Horario ocupado", "horario-ocupado", ex);
        problem.setProperty("fechaHora", ex.getFechaHora().toString());
        return problem;
    }

    @ExceptionHandler(TurnoYaCanceladoException.class)
    public ProblemDetail turnoYaCancelado(TurnoYaCanceladoException ex) {
        ProblemDetail problem = problema(HttpStatus.CONFLICT, "Turno ya cancelado", "turno-ya-cancelado", ex);
        problem.setProperty("turnoId", ex.getId());
        return problem;
    }

    @ExceptionHandler(FechaFeriadoException.class)
    public ProblemDetail fechaFeriado(FechaFeriadoException ex) {
        ProblemDetail problem = problema(HttpStatus.UNPROCESSABLE_CONTENT, "Fecha no disponible", "fecha-feriado", ex);
        problem.setProperty("feriado", ex.getFeriado().localName());
        return problem;
    }

    /** La API de feriados no respondió (solo llega acá desde /api/calendario). */
    @ExceptionHandler(RestClientException.class)
    public ProblemDetail servicioExterno(RestClientException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                "El servicio de feriados no está disponible. Probá de nuevo en unos minutos.");
        problem.setTitle("Servicio externo no disponible");
        problem.setType(URI.create(BASE_TYPE + "servicio-externo"));
        return problem;
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String tipo, Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setTitle(titulo);
        problem.setType(URI.create(BASE_TYPE + tipo));
        return problem;
    }
}
