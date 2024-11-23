package com.proyecto_de_aula.EducationalHarmonie_pa5.repository;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    // Buscar un grupo por nombre
    Optional<Grupo> findByNombre(String nombre);

    // Buscar grupos por docente asignado
    List<Grupo> findByDocenteUsername(String docenteUsername);

    // Buscar grupos por curso
    List<Grupo> findByCursoId(Long cursoId);
}






