package com.proyecto_de_aula.EducationalHarmonie_pa5.service;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.UserLoginRequest;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Usuario;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // Inyección de dependencia del repositorio para manejar usuarios en la base de datos
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Instancia del codificador BCryptPasswordEncoder para verificar las contraseñas
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Valida las credenciales del usuario durante el proceso de inicio de sesión.
     *
     * @param userLoginRequest Objeto que contiene las credenciales de inicio de sesión del usuario (nombre de usuario, contraseña, rol)
     * @return true si las credenciales son correctas, false si alguna validación falla
     */
    public boolean validarCredenciales(UserLoginRequest userLoginRequest) {
        // Buscar al usuario por su nombre de usuario en la base de datos
        Usuario usuario = usuarioRepository.findByNombreUsuario(userLoginRequest.getNombreUsuario());

        // Verificar que el usuario exista en la base de datos
        if (usuario == null) {
            return false;  // Usuario no encontrado
        }

        // Verificar que el rol del usuario coincida con el rol proporcionado en la solicitud de inicio de sesión
        if (!usuario.getRol().equalsIgnoreCase(userLoginRequest.getRol())) {
            return false;  // El rol no coincide
        }

        // Verificar que la contraseña proporcionada coincida con la contraseña almacenada en la base de datos
        // Se usa BCryptPasswordEncoder para comparar las contraseñas de forma segura
        return passwordEncoder.matches(userLoginRequest.getContrasena(), usuario.getContrasena());
    }

    /**
     * Cierra la sesión del usuario actual.
     *
     * Utiliza el contexto de seguridad de Spring Security para limpiar la sesión del usuario.
     * Este método puede incluir la invalidación del token JWT si es necesario.
     */
    public void cerrarSesion() {
        // Limpiar el contexto de seguridad (esto implica terminar la sesión del usuario)
        SecurityContextHolder.clearContext();

        // Si usas JWT, también puedes invalidar el token aquí, dependiendo de tu implementación.
        // Ejemplo: invalidateToken();
        System.out.println("Sesión cerrada con éxito.");
    }
}
