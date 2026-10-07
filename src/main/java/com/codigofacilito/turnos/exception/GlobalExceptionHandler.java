package com.codigofacilito.turnos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

/**
 * Traduce excepciones de dominio a respuestas RFC 9457 (application/problem+json).
 * Al extender ResponseEntityExceptionHandler, los errores propios de Spring MVC
 * (JSON mal formado, versión de API inválida, método no soportado...)
 * también salen como ProblemDetail.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String BASE_TYPE = "https://turnos-api.dev/errores/";

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

    private ProblemDetail problema(HttpStatus status, String titulo, String tipo, Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setTitle(titulo);
        problem.setType(URI.create(BASE_TYPE + tipo));
        return problem;
    }
}
