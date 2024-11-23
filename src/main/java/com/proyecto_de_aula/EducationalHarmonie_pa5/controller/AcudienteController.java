package com.proyecto_de_aula.EducationalHarmonie_pa5.controller;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Estudiante;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Queja;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Reporte;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Notificacion;
import com.proyecto_de_aula.EducationalHarmonie_pa5.service.AcudienteService;
import com.proyecto_de_aula.EducationalHarmonie_pa5.service.QuejaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/acudiente")
public class AcudienteController {

    @Autowired
    private AcudienteService acudienteService;

    @Autowired
    private QuejaService quejaService;

    /**
     * Lista los estudiantes asignados a un acudiente, con la opción de buscar por nombre o apellido.
     *
     * @param acudienteUsername El nombre de usuario del acudiente para identificarlo.
     * @param nombre (Opcional) Nombre del estudiante para realizar la búsqueda.
     * @param apellido (Opcional) Apellido del estudiante para realizar la búsqueda.
     * @return Una respuesta HTTP con la lista de estudiantes asignados al acudiente, filtrada según los criterios de búsqueda.
     */
    @GetMapping("/estudiantes")
    public ResponseEntity<List<Estudiante>> listarEstudiantesAsignados(
            @RequestParam String acudienteUsername,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido) {

        List<Estudiante> estudiantes = acudienteService.listarEstudiantesAsignados(acudienteUsername, nombre, apellido);
        return ResponseEntity.ok(estudiantes);
    }

    /**
     * Crea una queja para un estudiante específico, solo si el estudiante está asignado al acudiente
     * y el grupo tiene un docente asignado.
     *
     * @param acudienteUsername El nombre de usuario del acudiente que está creando la queja.
     * @param estudianteId El ID del estudiante para el cual se crea la queja.
     * @param queja El objeto que contiene los detalles de la queja.
     * @return Una respuesta HTTP con un mensaje de éxito o error según la validación.
     */
    @PostMapping("/crear-queja")
    public ResponseEntity<String> crearQueja(@RequestParam String acudienteUsername,
                                             @RequestParam Long estudianteId,
                                             @RequestBody Queja queja) {
        // Verificar si el estudiante está asignado al acudiente
        Estudiante estudiante = estudianteService.obtenerEstudiantePorId(estudianteId);
        if (estudiante == null || !estudiante.getAcudienteUsername().equals(acudienteUsername)) {
            return ResponseEntity.badRequest().body("El estudiante no está asignado a este acudiente.");
        }

        // Verificar si el estudiante tiene un grupo asignado y si el grupo tiene un docente
        Grupo grupo = estudiante.getGrupo();
        if (grupo == null || grupo.getDocente() == null) {
            return ResponseEntity.badRequest().body("El estudiante no tiene un grupo o el grupo no tiene un docente asignado.");
        }

        // Asignar el docente del grupo a la queja y guardar la queja
        queja.setEstudiante(estudiante);
        queja.setDocente(grupo.getDocente());  // Asigna el docente correspondiente
        quejaService.guardarQueja(queja);

        return ResponseEntity.ok("Queja creada con éxito.");
    }

    /**
     * Muestra el historial de quejas enviadas por el acudiente, con la opción de filtrar por palabra clave
     * o fecha.
     *
     * @param acudienteUsername El nombre de usuario del acudiente para obtener su historial de quejas.
     * @param keyword (Opcional) Palabra clave para filtrar las quejas.
     * @param fecha (Opcional) Fecha para filtrar las quejas.
     * @return Una respuesta HTTP con la lista de quejas enviadas por el acudiente, filtrada según los parámetros proporcionados.
     */
    @GetMapping("/historial-quejas")
    public ResponseEntity<List<Queja>> verHistorialQuejas(
            @RequestParam String acudienteUsername,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fecha) {

        List<Queja> quejas = quejaService.obtenerHistorialQuejas(acudienteUsername, keyword, fecha);
        return ResponseEntity.ok(quejas);
    }

    /**
     * Muestra el historial de reportes recibidos para un estudiante asignado a un acudiente, con filtros
     * por nombre, apellido, palabra clave o fecha.
     *
     * @param acudienteUsername El nombre de usuario del acudiente para obtener los reportes de sus estudiantes.
     * @param nombre (Opcional) Nombre del estudiante para filtrar los reportes.
     * @param apellido (Opcional) Apellido del estudiante para filtrar los reportes.
     * @param keyword (Opcional) Palabra clave para filtrar los reportes.
     * @param fecha (Opcional) Fecha para filtrar los reportes.
     * @return Una respuesta HTTP con los reportes filtrados.
     */
    @GetMapping("/historial-reportes")
    public ResponseEntity<?> verHistorialReportes(
            @RequestParam String acudienteUsername,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fecha) {

        List<Reporte> reportes = acudienteService.obtenerHistorialReportes(acudienteUsername, nombre, apellido, keyword, fecha);

        if (reportes.isEmpty()) {
            return ResponseEntity.badRequest().body("No se encontraron reportes o el estudiante no está asignado al acudiente.");
        }

        return ResponseEntity.ok(reportes);
    }

    /**
     * Obtiene las notificaciones enviadas al acudiente.
     *
     * @param acudienteUsername El nombre de usuario del acudiente para obtener sus notificaciones.
     * @return Una respuesta HTTP con la lista de notificaciones enviadas al acudiente.
     */
    @GetMapping("/notificaciones")
    public ResponseEntity<List<Notificacion>> verNotificaciones(@RequestParam String acudienteUsername) {
        List<Notificacion> notificaciones = acudienteService.obtenerNotificaciones(acudienteUsername);
        return ResponseEntity.ok(notificaciones);
    }

    /**
     * Marca una notificación como leída.
     *
     * @param notificacionId El ID de la notificación que se desea marcar como leída.
     * @return Una respuesta HTTP con un mensaje de éxito o error.
     */
    @PostMapping("/marcar-leida")
    public ResponseEntity<String> marcarNotificacionComoLeida(@RequestParam Long notificacionId) {
        boolean actualizada = notificacionService.marcarComoLeida(notificacionId);
        if (actualizada) {
            return ResponseEntity.ok("Notificación marcada como leída.");
        } else {
            return ResponseEntity.badRequest().body("Error al marcar la notificación como leída.");
        }
    }
}
