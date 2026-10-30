package com.codigofacilito.turnos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * NUEVO: Turno pasa de record a ENTIDAD JPA.
 *
 * ¿Por qué ya no es un record?
 *  - JPA necesita un constructor sin parámetros para crear el objeto al leer de la base.
 *  - JPA modifica los atributos (por ejemplo, asigna el id al guardar), y un record es inmutable.
 *
 * El JSON que devuelve la API no cambia: Jackson usa los getters (getId -> "id", getCliente -> "cliente"...).
 */
@Entity                 // esta clase se guarda en una tabla
@Table(name = "turno")  // ...que se llama "turno" (si no lo ponemos, usa el nombre de la clase)
public class Turno {

    @Id                                                   // la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // la genera la base (AUTO_INCREMENT)
    private Long id;

    @Column(nullable = false, length = 100)
    private String cliente;

    @Column(nullable = false, length = 100)
    private String servicio;

    @Column(name = "fecha_hora", nullable = false)        // el nombre de la columna en la tabla
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)                          // guarda "PENDIENTE", no un número
    @Column(nullable = false, length = 20)
    private EstadoTurno estado;

    /** Obligatorio para JPA. Es protected para que nadie lo use desde afuera */
    protected Turno() {
    }

    /** Un turno nuevo siempre nace PENDIENTE y sin id: el id lo asigna la base al guardar */
    public Turno(String cliente, String servicio, LocalDateTime fechaHora) {
        this.cliente = cliente;
        this.servicio = servicio;
        this.fechaHora = fechaHora;
        this.estado = EstadoTurno.PENDIENTE;
    }

    /** PUT: reemplaza los datos, conserva id y estado. */
    public void actualizar(String cliente, String servicio, LocalDateTime fechaHora) {
        this.cliente = cliente;
        this.servicio = servicio;
        this.fechaHora = fechaHora;
    }

    public void cancelar() {
        this.estado = EstadoTurno.CANCELADO;
    }

    public Long getId() { return id; }

    public String getCliente() { return cliente; }

    public String getServicio() { return servicio; }

    public LocalDateTime getFechaHora() { return fechaHora; }

    public EstadoTurno getEstado() { return estado; }
}
