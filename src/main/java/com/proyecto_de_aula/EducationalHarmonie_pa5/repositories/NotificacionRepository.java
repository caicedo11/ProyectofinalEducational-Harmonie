package com.proyecto_de_aula.EducationalHarmonie_pa5.repository;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Buscar notificaciones por grupo
    List<Notificacion> findByGrupoId(Long grupoId);

    // Buscar notificaciones por usuario
    List<Notificacion> findByUsuarioUsername(String username);

    // Actualizar el estado de la notificación a leída
    Optional<Notificacion> findById(Long id);

    // Usar un método personalizado para cambiar el estado a leída
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.id = :id")
    int marcarComoLeida(@Param("id") Long id);
}






