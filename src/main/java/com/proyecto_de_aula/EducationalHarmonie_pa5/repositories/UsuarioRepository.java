package com.proyecto_de_aula.EducationalHarmonie_pa5.repository;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por nombre de usuario
    Optional<Usuario> findByUsername(String username);

    // Buscar usuarios por rol (docente, acudiente, admin)
    List<Usuario> findByRol(Usuario.Rol rol);

    // Buscar usuarios por nombre o apellido
    List<Usuario> findByNombreContainingOrApellidoContaining(String nombre, String apellido);
}





