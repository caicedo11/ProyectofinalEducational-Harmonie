package com.proyecto_de_aula.EducationalHarmonie_pa5.repository;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // Buscar reportes por estudiante
    List<Reporte> findByEstudianteId(Long estudianteId);

    // Buscar reportes por estudiante y fecha
    List<Reporte> findByEstudianteIdAndFecha(Long estudianteId, String fecha);

    // Buscar reportes por palabras clave en la descripción
    @Query("SELECT r FROM Reporte r WHERE r.descripcion LIKE %:keyword%")
    List<Reporte> findByDescripcionContaining(@Param("keyword") String keyword);

    // Buscar reportes por palabras clave en la descripción y estudiante
    @Query("SELECT r FROM Reporte r WHERE r.estudianteId = :estudianteId AND r.descripcion LIKE %:keyword%")
    List<Reporte> findByEstudianteIdAndDescripcionContaining(@Param("estudianteId") Long estudianteId, @Param("keyword") String keyword);
}

