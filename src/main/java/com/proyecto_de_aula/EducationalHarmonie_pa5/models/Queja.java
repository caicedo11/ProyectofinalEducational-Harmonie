package com.proyecto_de_aula.EducationalHarmonie_pa5.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "quejas")
public class Queja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    private String asunto;  // Agregado para el asunto de la queja

    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "acudiente_id")
    private Usuario acudiente;  // La queja es enviada por el acudiente

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Usuario docente;  // La queja es enviada al docente

    private boolean leida;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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



