package com.proyecto_de_aula.EducationalHarmonie_pa5.repository;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Queja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuejaRepository extends JpaRepository<Queja, Long> {


    // Buscar quejas por docente
    List<Queja> findByDocenteUsername(String docenteUsername);

    // Buscar quejas por acudiente
    List<Queja> findByAcudienteId(Long acudienteId);

    // Buscar quejas por palabra clave en la descripción
    @Query("SELECT q FROM Queja q WHERE q.descripcion LIKE %:keyword%")
    List<Queja> findByDescripcionContaining(@Param("keyword") String keyword);

    // Buscar quejas por estudiante y palabra clave en la descripción
    @Query("SELECT q FROM Queja q WHERE q.estudianteId = :estudianteId AND q.descripcion LIKE %:keyword%")
    List<Queja> findByEstudianteIdAndDescripcionContaining(@Param("estudianteId") Long estudianteId, @Param("keyword") String keyword);
}



