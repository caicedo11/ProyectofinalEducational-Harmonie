package com.proyecto_de_aula.EducationalHarmonie_pa5.repository;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Buscar estudiantes por nombre o apellido
    List<Estudiante> findByNombreContainingAndApellidoContaining(String nombre, String apellido);

    // Buscar estudiantes por grupo
    List<Estudiante> findByGrupoId(Long grupoId);

    // Buscar estudiantes por grupo y nombre de usuario de docente
    List<Estudiante> findByGrupoDocenteUsername(String docenteUsername);

    // Buscar estudiante por nombre, apellido y grupo
    Estudiante findByNombreAndApellidoAndGrupoId(String nombre, String apellido, Long grupoId);
}








