package com.proyecto_de_aula.EducationalHarmonie_pa5.controller;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Estudiante;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Queja;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Reporte;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Notificacion;
import com.proyecto_de_aula.EducationalHarmonie_pa5.service.DocenteService;
import com.proyecto_de_aula.EducationalHarmonie_pa5.service.QuejaService;
import com.proyecto_de_aula.EducationalHarmonie_pa5.service.ReporteService;
import com.proyecto_de_aula.EducationalHarmonie_pa5.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docente")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private QuejaService quejaService;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Listar el grupo asignado al docente, permitiendo la búsqueda de estudiantes por nombre o apellido.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param nombre Nombre del estudiante (opcional).
     * @param apellido Apellido del estudiante (opcional).
     * @return ResponseEntity con la lista de estudiantes que cumplen los criterios de búsqueda.
     */
    @GetMapping("/grupo")
    public ResponseEntity<?> listarGrupoAsignado(@RequestParam String docenteUsername,
                                                 @RequestParam(required = false) String nombre,
                                                 @RequestParam(required = false) String apellido) {
        List<Estudiante> estudiantes = docenteService.listarGrupoAsignado(docenteUsername, nombre, apellido);

        if (estudiantes.isEmpty()) {
            return ResponseEntity.status(404).body("Estudiantes no encontrados en el grupo o no coinciden con los criterios de búsqueda.");
        }

        return ResponseEntity.ok(estudiantes);
    }

    /**
     * Crear un reporte sobre un estudiante específico.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param nombre Nombre del estudiante (opcional).
     * @param apellido Apellido del estudiante (opcional).
     * @param estudianteId ID del estudiante.
     * @param reporte Datos del reporte a crear.
     * @return ResponseEntity con el resultado de la creación del reporte.
     */
    @PostMapping("/crear-reporte")
    public ResponseEntity<String> crearReporte(@RequestParam String docenteUsername,
                                               @RequestParam(required = false) String nombre,
                                               @RequestParam(required = false) String apellido,
                                               @RequestParam Long estudianteId,
                                               @RequestBody Reporte reporte) {

        if (nombre != null || apellido != null) {
            Estudiante estudiante = docenteService.buscarEstudiantePorNombreApellido(docenteUsername, nombre, apellido);
            if (estudiante == null) {
                return ResponseEntity.badRequest().body("Estudiante no encontrado en su grupo o no coincide con los criterios.");
            }
            estudianteId = estudiante.getId();
        }

        boolean creado = reporteService.crearReporte(docenteUsername, estudianteId, reporte);
        if (creado) {
            return ResponseEntity.ok("Reporte creado con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al crear el reporte. Verifique los datos ingresados.");
        }
    }

    /**
     * Ver el historial de reportes de un estudiante, permitiendo la búsqueda por nombre, apellido, palabra clave o fecha.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param estudianteId ID del estudiante (opcional si se proporciona nombre o apellido).
     * @param nombre Nombre del estudiante (opcional).
     * @param apellido Apellido del estudiante (opcional).
     * @param palabraClave Palabra clave para filtrar los reportes (opcional).
     * @param fecha Fecha para filtrar los reportes (opcional).
     * @return ResponseEntity con la lista de reportes encontrados.
     */
    @GetMapping("/historial-reportes")
    public ResponseEntity<List<Reporte>> verHistorialReportes(@RequestParam String docenteUsername,
                                                              @RequestParam(required = false) Long estudianteId,
                                                              @RequestParam(required = false) String nombre,
                                                              @RequestParam(required = false) String apellido,
                                                              @RequestParam(required = false) String palabraClave,
                                                              @RequestParam(required = false) String fecha) {

        if (nombre != null || apellido != null) {
            Estudiante estudiante = docenteService.buscarEstudiantePorNombreApellido(docenteUsername, nombre, apellido);
            if (estudiante == null) {
                return ResponseEntity.badRequest().body(null);
            }
            estudianteId = estudiante.getId();
        }

        List<Reporte> reportes = reporteService.obtenerHistorialReportes(estudianteId, palabraClave, fecha);

        return ResponseEntity.ok(reportes);
    }

    /**
     * Ver las quejas enviadas por los acudientes para el grupo del docente.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @return ResponseEntity con la lista de quejas recibidas.
     */
    @GetMapping("/quejas")
    public ResponseEntity<List<Queja>> verQuejasRecibidas(@RequestParam String docenteUsername) {
        List<Queja> quejas = quejaService.obtenerQuejasRecibidas(docenteUsername);
        return ResponseEntity.ok(quejas);
    }

    /**
     * Marcar una queja como leída.
     *
     * @param quejaId ID de la queja a marcar como leída.
     * @return ResponseEntity con el resultado de la acción.
     */
    @PutMapping("/queja/{quejaId}/marcar-como-leida")
    public ResponseEntity<String> marcarQuejaComoLeida(@PathVariable Long quejaId) {
        boolean marcada = quejaService.marcarComoLeida(quejaId);
        if (marcada) {
            return ResponseEntity.ok("Queja marcada como leída con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al marcar la queja como leída.");
        }
    }

    /**
     * Ver historial de quejas enviadas por los acudientes al docente.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param nombre Nombre del acudiente (opcional).
     * @param apellido Apellido del acudiente (opcional).
     * @param palabraClave Palabra clave para filtrar las quejas (opcional).
     * @param fecha Fecha para filtrar las quejas (opcional).
     * @return ResponseEntity con la lista de quejas encontradas.
     */
    @GetMapping("/historial-quejas")
    public ResponseEntity<List<Queja>> verHistorialQuejas(@RequestParam String docenteUsername,
                                                          @RequestParam(required = false) String nombre,
                                                          @RequestParam(required = false) String apellido,
                                                          @RequestParam(required = false) String palabraClave,
                                                          @RequestParam(required = false) String fecha) {

        List<Queja> quejas = quejaService.obtenerHistorialQuejasDocente(docenteUsername, nombre, apellido, palabraClave, fecha);
        return ResponseEntity.ok(quejas);
    }

    /**
     * Enviar notificaciones a los acudientes.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param notificacionRequest Datos de la notificación a enviar.
     * @return ResponseEntity con el resultado de la acción de envío.
     */
    @PostMapping("/enviar-notificacion")
    public ResponseEntity<String> enviarNotificacion(@RequestParam String docenteUsername,
                                                     @RequestBody NotificacionRequest notificacionRequest) {
        boolean notificacionEnviada = notificacionService.enviarNotificacion(docenteUsername, notificacionRequest);

        if (notificacionEnviada) {
            return ResponseEntity.ok("Notificación enviada con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al enviar la notificación.");
        }
    }
}
