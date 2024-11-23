package com.proyecto_de_aula.EducationalHarmonie_pa5.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String asunto;

    @NotNull
    private String mensaje;

    @NotNull
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "acudiente_id", nullable = true)
    private Usuario acudiente;

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = true)
    private Usuario docente;

    private boolean leida;

    public Notificacion() {}

    public Notificacion(String asunto, String mensaje, Usuario acudiente, Usuario docente) {
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.acudiente = acudiente;
        this.docente = docente;
        this.fecha = LocalDateTime.now();
        this.leida = false;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Usuario getAcudiente() {
        return acudiente;
    }

    public void setAcudiente(Usuario acudiente) {
        this.acudiente = acudiente;
    }

    public Usuario getDocente() {
        return docente;
    }

    public void setDocente(Usuario docente) {
        this.docente = docente;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }
}
