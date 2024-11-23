package com.proyecto_de_aula.EducationalHarmonie_pa5.service;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Estudiante;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Grupo;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Reporte;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Queja;
import com.proyecto_de_aula.EducationalHarmonie_pa5.model.Notificacion;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.GrupoRepository;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.EstudianteRepository;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.ReporteRepository;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.QuejaRepository;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@Service
public class DocenteService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private QuejaRepository quejaRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    /**
     * Lista los estudiantes del grupo asignado a un docente, con la opción de filtrar por nombre y apellido.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param nombre          Nombre del estudiante para filtrar (opcional).
     * @param apellido        Apellido del estudiante para filtrar (opcional).
     * @return Lista de estudiantes del grupo asignado al docente.
     */
    public List<Estudiante> listarGrupoAsignado(String docenteUsername, String nombre, String apellido) {
        // Buscar el grupo del docente
        Grupo grupo = grupoRepository.findByDocenteUsername(docenteUsername);
        if (grupo == null) {
            return List.of(); // Si no tiene grupo asignado, devolver lista vacía
        }

        // Si se proporciona nombre o apellido, buscar estudiantes con los filtros aplicados
        if (nombre != null || apellido != null) {
            return estudianteRepository.findByGrupoAndNombreContainingAndApellidoContaining(grupo, nombre != null ? nombre : "", apellido != null ? apellido : "");
        }

        // Si no se filtra por nombre ni apellido, devolver todos los estudiantes del grupo
        return estudianteRepository.findByGrupo(grupo);
    }

    /**
     * Crea un reporte para un estudiante específico asignado al docente.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param estudianteId    ID del estudiante al que se le crea el reporte.
     * @param reporte         Objeto Reporte con los detalles del reporte.
     * @return `true` si el reporte se crea exitosamente, `false` si no es posible crear el reporte.
     */
    public boolean crearReporte(String docenteUsername, Long estudianteId, Reporte reporte) {
        // Verificar si el docente tiene un grupo asignado
        Grupo grupo = grupoRepository.findByDocenteUsername(docenteUsername);
        if (grupo == null) {
            return false; // El docente no tiene un grupo asignado
        }

        // Verificar si el estudiante pertenece al grupo del docente
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        if (estudiante == null || !estudiante.getGrupo().equals(grupo)) {
            return false; // El estudiante no está en el grupo del docente
        }

        // Asignar los valores del reporte
        reporte.setFecha(new Date()); // Asignar la fecha actual
        reporte.setEstudiante(estudiante); // Asignar el estudiante
        reporte.setDocenteUsername(docenteUsername); // Asignar el docente

        // Guardar el reporte
        reporteRepository.save(reporte);

        return true; // Reporte creado con éxito
    }

    /**
     * Busca un estudiante por nombre y apellido dentro del grupo asignado al docente.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param nombre          Nombre del estudiante.
     * @param apellido        Apellido del estudiante.
     * @return Objeto Estudiante que coincide con los criterios de búsqueda.
     */
    public Estudiante buscarEstudiantePorNombreApellido(String docenteUsername, String nombre, String apellido) {
        // Validar si el docente tiene el estudiante en su grupo
        return estudianteRepository.findByNombreAndApellidoAndDocenteUsername(nombre, apellido, docenteUsername);
    }

    /**
     * Obtiene el historial de reportes de un estudiante, con filtros por palabra clave o fecha.
     *
     * @param estudianteId  ID del estudiante cuyo historial de reportes se desea obtener.
     * @param palabraClave  Palabra clave para filtrar los reportes.
     * @param fecha         Fecha para filtrar los reportes.
     * @return Lista de reportes filtrados según los parámetros proporcionados.
     */
    public List<Reporte> obtenerHistorialReportes(Long estudianteId, String palabraClave, String fecha) {
        Date fechaParseada = null;

        // Si se proporciona fecha, convertirla en un objeto Date
        if (fecha != null && !fecha.isEmpty()) {
            try {
                fechaParseada = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
            } catch (ParseException e) {
                return null; // Manejar error de formato de fecha
            }
        }

        // Filtrar por palabra clave y/o fecha
        if (palabraClave != null && fechaParseada != null) {
            return reporteRepository.findByEstudianteIdAndPalabraClaveAndFecha(estudianteId, palabraClave, fechaParseada);
        } else if (palabraClave != null) {
            return reporteRepository.findByEstudianteIdAndPalabraClave(estudianteId, palabraClave);
        } else if (fechaParseada != null) {
            return reporteRepository.findByEstudianteIdAndFecha(estudianteId, fechaParseada);
        } else {
            return reporteRepository.findByEstudianteId(estudianteId); // Obtener todos los reportes
        }
    }

    /**
     * Obtiene las quejas enviadas por los acudientes al docente.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @return Lista de quejas enviadas al docente.
     */
    public List<Queja> obtenerQuejasRecibidas(String docenteUsername) {
        return quejaRepository.findByDocenteUsername(docenteUsername);
    }

    /**
     * Marca una queja como leída.
     *
     * @param quejaId ID de la queja a marcar como leída.
     * @return `true` si la queja se marcó como leída exitosamente, `false` si la queja no existe.
     */
    public boolean marcarQuejaComoLeida(Long quejaId) {
        Queja queja = quejaRepository.findById(quejaId).orElse(null);
        if (queja != null) {
            queja.setLeida(true);
            quejaRepository.save(queja);
            return true;
        }
        return false;  // Si la queja no se encuentra, retornar false
    }

    /**
     * Obtiene el historial de quejas enviadas por los acudientes a un docente específico.
     * Permite filtrar por nombre, apellido, palabra clave y fecha.
     *
     * @param docenteUsername Nombre de usuario del docente.
     * @param nombre          Nombre del acudiente para filtrar.
     * @param apellido        Apellido del acudiente para filtrar.
     * @param palabraClave    Palabra clave en el contenido de la queja para filtrar.
     * @param fecha           Fecha de la queja para filtrar.
     * @return Listado de quejas filtradas.
     */
    public List<Queja> obtenerHistorialQuejasDocente(String docenteUsername,
                                                     String nombre,
                                                     String apellido,
                                                     String palabraClave,
                                                     String fecha) {
        if (nombre == null && apellido == null && palabraClave == null && fecha == null) {
            return quejaRepository.findByDocenteUsername(docenteUsername); // Obtener todas las quejas para el docente
        }

        // Filtrar por nombre y apellido, palabra clave o fecha según los parámetros proporcionados
        if (nombre != null && apellido != null) {
            return quejaRepository.findByDocenteUsernameAndAcudienteNombreApellido(docenteUsername, nombre, apellido);
        }

        if (palabraClave != null) {
            return quejaRepository.findByDocenteUsernameAndContenidoContaining(docenteUsername, palabraClave);
        }

        if (fecha != null) {
            return quejaRepository.findByDocenteUsernameAndFecha(docenteUsername, fecha);
        }

        return quejaRepository.findByDocenteUsername(docenteUsername); // Si no se proporcionan filtros, devolver todas las quejas
    }

    /**
     * Envía una notificación a los acudientes seleccionados.
     *
     * @param docenteUsername     Nombre de usuario del docente que envía la notificación.
     * @param notificacionRequest Objeto con los detalles de la notificación (asunto y mensaje).
     * @return `true` si la notificación se envía correctamente, `false` si hubo un error.
     */
    public boolean enviarNotificacion(String docenteUsername, Notificacion notificacionRequest) {
        // Verificar si el docente tiene un grupo asignado
        Grupo grupo = grupoRepository.findByDocenteUsername(docenteUsername);
        if (grupo == null) {
            return false; // El docente no tiene grupo asignado
        }

        // Crear la notificación
        Notificacion notificacion = new Notificacion();
        notificacion.setDocenteUsername(docenteUsername);
        notificacion.setAsunto(notificacionRequest.getAsunto());
        notificacion.setMensaje(notificacionRequest.getMensaje());
        notificacion.setFecha(new Date());

        // Guardar la notificación
        notificacionRepository.save(notificacion);

        // Enviar la notificación a los acudientes del grupo
        for (Estudiante estudiante : grupo.getEstudiantes()) {
            for (Acudiente acudiente : estudiante.getAcudientes()) {
                acudiente.getNotificaciones().add(notificacion);
            }
        }

        return true; // Notificación enviada con éxito
    }
}


