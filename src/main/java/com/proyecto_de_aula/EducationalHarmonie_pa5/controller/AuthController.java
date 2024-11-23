package com.proyecto_de_aula.EducationalHarmonie_pa5.controller;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.UserLoginRequest;
import com.proyecto_de_aula.EducationalHarmonie_pa5.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Inyección de dependencia del servicio de autenticación
    @Autowired
    private AuthService authService;

    /**
     * Endpoint para iniciar sesión en el sistema.
     *
     * Recibe las credenciales de inicio de sesión (nombre de usuario, contraseña y rol)
     * y valida si el usuario tiene acceso al sistema.
     * Si las credenciales son correctas, se realiza el inicio de sesión.
     *
     * @param userLoginRequest Objeto que contiene las credenciales del usuario (nombre de usuario, contraseña, rol).
     * @return ResponseEntity con un mensaje de éxito si las credenciales son correctas, o un mensaje de error si no lo son.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest userLoginRequest) {
        // Llamada al servicio para validar las credenciales del usuario
        boolean autenticado = authService.validarCredenciales(userLoginRequest);

        // Si las credenciales son válidas, el usuario es autenticado correctamente
        if (autenticado) {
            return ResponseEntity.ok("Inicio de sesión exitoso.");
        } else {
            // Si las credenciales son incorrectas, se retorna un mensaje de error
            return ResponseEntity.badRequest().body("Credenciales incorrectas.");
        }
    }

    /**
     * Endpoint para cerrar la sesión del usuario actual.
     *
     * Llama al servicio de autenticación para limpiar la sesión y finalizar la sesión activa.
     *
     * @return ResponseEntity con un mensaje de éxito al cerrar la sesión.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Llamada al servicio para cerrar la sesión
        authService.cerrarSesion();

        // Mensaje de éxito tras cerrar la sesión
        return ResponseEntity.ok("Sesión cerrada con éxito.");
    }
}
