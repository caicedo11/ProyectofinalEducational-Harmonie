package com.proyecto_de_aula.EducationalHarmonie_pa5.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;  // Relación con el estudiante al que se le hace el reporte

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Usuario docente;  // Relación con el docente que genera el reporte

    private String tipoIncidente;  // Tipo de incidente

    private String descripcion;    // Descripción detallada del incidente

    private String otrosImplicados;  // Otros implicados en el incidente (si los hay)

    private String acciones;  // Acciones que se deben tomar

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

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Usuario getDocente() {
        return docente;
    }

    public void setDocente(Usuario docente) {
        this.docente = docente;
    }

    public String getTipoIncidente() {
        return tipoIncidente;
    }

    public void setTipoIncidente(String tipoIncidente) {
        this.tipoIncidente = tipoIncidente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getOtrosImplicados() {
        return otrosImplicados;
    }

    public void setOtrosImplicados(String otrosImplicados) {
        this.otrosImplicados = otrosImplicados;
    }

    public String getAcciones() {
        return acciones;
    }

    public void setAcciones(String acciones) {
        this.acciones = acciones;
    }
}

