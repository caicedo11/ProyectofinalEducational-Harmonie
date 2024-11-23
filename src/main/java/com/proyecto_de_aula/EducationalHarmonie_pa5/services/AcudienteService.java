package com.proyecto_de_aula.EducationalHarmonie_pa5.service;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Estudiante;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Queja;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Reporte;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Notificacion;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.EstudianteRepository;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.QuejaRepository;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.ReporteRepository;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los acudientes.
 * Permite la creación de quejas, obtención de historiales de quejas, reportes y notificaciones de los acudientes.
 */
@Service
public class AcudienteService {

    @Autowired
    private EstudianteRepository estudianteRepository; // Repositorio para estudiantes

    @Autowired
    private QuejaRepository quejaRepository; // Repositorio para quejas

    @Autowired
    private ReporteRepository reporteRepository; // Repositorio para reportes

    @Autowired
    private NotificacionRepository notificacionRepository; // Repositorio para notificaciones

    /**
     * Obtiene la lista de estudiantes asignados a un acudiente específico.
     * Permite filtrar por nombre y apellido si se proporcionan como parámetros.
     *
     * @param acudienteUsername Nombre de usuario del acudiente.
     * @param nombre Nombre del estudiante (opcional).
     * @param apellido Apellido del estudiante (opcional).
     * @return Lista de estudiantes asignados al acudiente, filtrados por nombre y apellido si se proporcionan.
     */
    public List<Estudiante> listarEstudiantesAsignados(String acudienteUsername, String nombre, String apellido) {
        List<Estudiante> estudiantesAsignados = estudianteRepository.findByAcudienteUsername(acudienteUsername);

        // Filtrar por nombre si el parámetro está presente
        if (nombre != null && !nombre.isEmpty()) {
            estudiantesAsignados = estudiantesAsignados.stream()
                    .filter(estudiante -> estudiante.getNombre().equalsIgnoreCase(nombre))
                    .collect(Collectors.toList());
        }

        // Filtrar por apellido si el parámetro está presente
        if (apellido != null && !apellido.isEmpty()) {
            estudiantesAsignados = estudiantesAsignados.stream()
                    .filter(estudiante -> estudiante.getApellido().equalsIgnoreCase(apellido))
                    .collect(Collectors.toList());
        }

        return estudiantesAsignados;
    }

    /**
     * Crea una nueva queja para un estudiante específico.
     * Verifica si el estudiante está asignado al acudiente antes de crear la queja.
     *
     * @param acudienteUsername Nombre de usuario del acudiente.
     * @param estudianteId ID del estudiante.
     * @param queja Detalles de la queja a crear.
     * @return `true` si la queja fue creada correctamente, `false` en caso contrario.
     */
    public boolean crearQueja(String acudienteUsername, Long estudianteId, Queja queja) {
        // Verifica si el estudiante está asignado al acudiente antes de crear la queja
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        if (estudiante != null && estudiante.getAcudienteUsername().equals(acudienteUsername)) {
            queja.setEstudiante(estudiante);
            quejaRepository.save(queja);
            return true;
        }
        return false;
    }

    /**
     * Crea una nueva queja para un estudiante específico, asignando el docente del grupo del estudiante.
     * Verifica que el estudiante esté asignado al acudiente y que el estudiante tenga un grupo con docente.
     *
     * @param acudienteUsername Nombre de usuario del acudiente.
     * @param estudianteId ID del estudiante.
     * @param queja Detalles de la queja a crear.
     * @return `true` si la queja fue creada correctamente, `false` en caso contrario.
     */
    public boolean crearQueja(String acudienteUsername, Long estudianteId, Queja queja) {
        // Verifica si el estudiante está asignado al acudiente
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        if (estudiante == null || !estudiante.getAcudienteUsername().equals(acudienteUsername)) {
            return false;
        }

        // Verifica si el estudiante tiene un grupo asignado y si el grupo tiene un docente
        Grupo grupo = estudiante.getGrupo();
        if (grupo == null || grupo.getDocente() == null) {
            return false;
        }

        // Asigna el docente del grupo al queja
        queja.setEstudiante(estudiante);
        queja.setDocente(grupo.getDocente());  // Asigna la queja al docente del grupo
        quejaRepository.save(queja);
        return true;
    }

    /**
     * Obtiene el historial de quejas de un acudiente.
     * Permite filtrar las quejas por palabra clave y fecha.
     *
     * @param acudienteUsername Nombre de usuario del acudiente.
     * @param keyword Palabra clave para filtrar las quejas (opcional).
     * @param fecha Fecha para filtrar las quejas (opcional).
     * @return Lista de quejas filtradas según los criterios proporcionados.
     */
    public List<Queja> obtenerHistorialQuejas(String acudienteUsername, String keyword, String fecha) {
        // Obtiene todas las quejas del acudiente
        List<Queja> quejas = quejaRepository.findByAcudienteUsername(acudienteUsername);

        // Filtrar por palabra clave si está presente
        if (keyword != null && !keyword.isEmpty()) {
            quejas = quejas.stream()
                    .filter(queja -> queja.getTitulo().contains(keyword) || queja.getDescripcion().contains(keyword))
                    .collect(Collectors.toList());
        }

        // Filtrar por fecha si está presente
        if (fecha != null && !fecha.isEmpty()) {
            LocalDate fechaFiltro = LocalDate.parse(fecha);
            quejas = quejas.stream()
                    .filter(queja -> queja.getFecha().toLocalDate().equals(fechaFiltro))
                    .collect(Collectors.toList());
        }

        return quejas;
    }

    /**
     * Obtiene el historial de reportes de los estudiantes asignados a un acudiente.
     * Permite filtrar por nombre, apellido, palabra clave y fecha.
     *
     * @param acudienteUsername Nombre de usuario del acudiente.
     * @param nombre Nombre del estudiante para filtrar (opcional).
     * @param apellido Apellido del estudiante para filtrar (opcional).
     * @param keyword Palabra clave para filtrar los reportes (opcional).
     * @param fecha Fecha para filtrar los reportes (opcional).
     * @return Lista de reportes filtrados según los criterios proporcionados.
     */
    public List<Reporte> obtenerHistorialReportes(String acudienteUsername, String nombre, String apellido, String keyword, String fecha) {
        // Obtiene los estudiantes asignados al acudiente
        List<Estudiante> estudiantesAsignados = estudianteRepository.findByAcudienteUsername(acudienteUsername);

        // Filtrar estudiantes asignados por nombre y apellido, si se proporcionan
        List<Estudiante> estudiantesFiltrados = estudiantesAsignados.stream()
                .filter(estudiante -> (nombre == null || estudiante.getNombre().equalsIgnoreCase(nombre)) &&
                        (apellido == null || estudiante.getApellido().equalsIgnoreCase(apellido)))
                .collect(Collectors.toList());

        if (estudiantesFiltrados.isEmpty()) {
            return Collections.emptyList(); // Devuelve una lista vacía si no se encuentra un estudiante asignado con el nombre/apellido
        }

        // Obtener reportes para los estudiantes filtrados
        List<Reporte> reportes = new ArrayList<>();
        for (Estudiante estudiante : estudiantesFiltrados) {
            reportes.addAll(reporteRepository.findByEstudianteId(estudiante.getId()));
        }

        // Aplicar filtro de palabra clave si está presente
        if (keyword != null && !keyword.isEmpty()) {
            reportes = reportes.stream()
                    .filter(reporte -> reporte.getTitulo().contains(keyword) || reporte.getDescripcion().contains(keyword))
                    .collect(Collectors.toList());
        }

        // Aplicar filtro de fecha si está presente
        if (fecha != null && !fecha.isEmpty()) {
            LocalDate fechaFiltro = LocalDate.parse(fecha);
            reportes = reportes.stream()
                    .filter(reporte -> reporte.getFecha().toLocalDate().equals(fechaFiltro))
                    .collect(Collectors.toList());
        }

        return reportes;
    }

    /**
     * Obtiene todas las notificaciones de un acudiente.
     *
     * @param acudienteUsername Nombre de usuario del acudiente.
     * @return Lista de notificaciones asociadas al acudiente.
     */
    public List<Notificacion> obtenerNotificaciones(String acudienteUsername) {
        // Obtiene todas las notificaciones del acudiente
        return notificacionRepository.findByAcudienteUsername(acudienteUsername);
    }

    /**
     * Marca una notificación como leída.
     *
     * @param notificacionId ID de la notificación a marcar como leída.
     */
    public void marcarNotificacionComoLeida(Long notificacionId) {
        // Obtiene la notificación por su ID y la marca como leída
        Notificacion notificacion = notificacionRepository.findById(notificacionId).orElse(null);
        if (notificacion != null) {
            notificacion.setLeida(true);
            notificacionRepository.save(notificacion);
        }
    }
}

