package com.proyecto_de_aula.EducationalHarmonie_pa5.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El nombre del grupo debe tener un formato específico: número + letra (ejemplo: 1A, 1B)
    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Usuario docente;

    // Relación con los estudiantes, un grupo tiene muchos estudiantes
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estudiante> estudiantes;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getDocente() {
        return docente;
    }

    public void setDocente(Usuario docente) {
        this.docente = docente;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    // Método para asegurar que el nombre del grupo sea válido (por ejemplo, 1A, 2B)
    public static boolean esNombreValido(String nombre) {
        return nombre != null && nombre.matches("\\d+[A-Za-z]");  // Asegura que sea un número seguido de una letra
    }
}
