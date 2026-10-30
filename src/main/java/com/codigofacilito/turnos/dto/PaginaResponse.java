package com.codigofacilito.turnos.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * NUEVO (P2-LC3): lo que devuelve un listado paginado.
 * Usamos un DTO propio en vez de devolver el Page de Spring tal cual:
 * así el contrato de la API lo decidimos nosotros y no cambia si cambia Spring.
 */
public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas,
        boolean ultima
) {
    /** Convierte un Page de entidades en una página de DTOs. */
    public static <E, T> PaginaResponse<T> from(Page<E> page, Function<E, T> convertir) {
        return new PaginaResponse<>(
                page.getContent().stream().map(convertir).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
