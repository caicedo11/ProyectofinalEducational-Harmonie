package com.proyecto_de_aula.EducationalHarmonie_pa5.service;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.*;
import com.proyecto_de_aula.EducationalHarmonie_pa5.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ReporteRepository reporteRepository;
    @Autowired
    private QuejaRepository quejaRepository;
    @Autowired
    private GrupoRepository grupoRepository;
    @Autowired
    private NotificacionRepository notificacionRepository;

    // Registrar usuario con validación de datos
    /**
     * Este método permite registrar un nuevo usuario, validando que no haya
     * un duplicado en el nombre de usuario y que los datos sean correctos.
     *
     * @param usuario Objeto Usuario que contiene la información del nuevo usuario.
     * @return true si el usuario fue registrado exitosamente, false si hubo algún error.
     */
    public boolean registrarUsuario(Usuario usuario) {
        // Validar que el nombre de usuario no esté duplicado
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            return false; // El nombre de usuario ya existe en la base de datos
        }

        // Validar que el rol esté presente y sea un valor válido
        if (!esRolValido(usuario.getRol())) {
            return false; // El rol no es válido
        }

        // Validar si el usuario tiene todos los campos requeridos
        if (usuario.getNombre() == null || usuario.getApellido() == null || usuario.getEmail() == null ||
                usuario.getUsername() == null || usuario.getPassword() == null || usuario.getRol() == null) {
            return false; // Algunos datos obligatorios están faltando
        }

        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);
        return true;
    }

    // Método para verificar si el rol es válido
    /**
     * Verifica si el rol del usuario es válido.
     *
     * @param rol El rol del usuario a validar.
     * @return true si el rol es válido (docente o acudiente), false si no lo es.
     */
    private boolean esRolValido(String rol) {
        return rol != null && (rol.equals("docente") || rol.equals("acudiente"));
    }

    // Listar usuarios con filtros por rol, nombre o apellido
    /**
     * Lista los usuarios según el filtro proporcionado (por rol, nombre o apellido).
     *
     * @param rol El rol de los usuarios a filtrar (puede ser null).
     * @param nombre El nombre de los usuarios a filtrar (puede ser null).
     * @param apellido El apellido de los usuarios a filtrar (puede ser null).
     * @return Una lista de usuarios que cumplen con el filtro.
     */
    public List<Usuario> listarUsuarios(String rol, String nombre, String apellido) {
        if (rol != null && !rol.isEmpty()) {
            return usuarioRepository.findByRol(rol); // Filtrar por rol
        } else if (nombre != null && !nombre.isEmpty()) {
            return usuarioRepository.findByNombreContainingIgnoreCase(nombre); // Filtrar por nombre
        } else if (apellido != null && !apellido.isEmpty()) {
            return usuarioRepository.findByApellidoContainingIgnoreCase(apellido); // Filtrar por apellido
        }
        return usuarioRepository.findAll(); // Sin filtro, retorna todos los usuarios
    }

    // Obtener un usuario por ID
    /**
     * Obtiene un usuario por su ID.
     *
     * @param id El ID del usuario.
     * @return Un Optional con el usuario encontrado, o vacío si no existe.
     */
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id); // Obtener el usuario por su ID
    }

    // Editar usuario
    /**
     * Edita los datos de un usuario existente en la base de datos.
     *
     * @param id El ID del usuario a editar.
     * @param usuario Objeto Usuario con los nuevos datos.
     * @return true si el usuario fue editado exitosamente, false si no fue encontrado.
     */
    public boolean editarUsuario(Long id, Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuarioActualizado = usuarioExistente.get();

            // Actualizar los campos del usuario
            usuarioActualizado.setNombre(usuario.getNombre());
            usuarioActualizado.setApellido(usuario.getApellido());
            usuarioActualizado.setEmail(usuario.getEmail());
            usuarioActualizado.setUsername(usuario.getUsername());
            usuarioActualizado.setPassword(usuario.getPassword());
            usuarioActualizado.setRol(usuario.getRol());

            usuarioRepository.save(usuarioActualizado);
            return true;
        }
        return false; // Si el usuario no existe, no se puede editar
    }

    // Eliminar usuario
    /**
     * Elimina un usuario de la base de datos.
     *
     * @param id El ID del usuario a eliminar.
     * @return true si el usuario fue eliminado exitosamente, false si no se encontró.
     */
    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id); // Eliminar el usuario
            return true;
        }
        return false; // Si el usuario no existe, no se puede eliminar
    }

    // Obtener historial de reportes de un estudiante con filtros opcionales
    /**
     * Obtiene el historial de reportes de un estudiante, con filtros opcionales como
     * palabra clave y fecha.
     *
     * @param nombre El nombre del estudiante.
     * @param apellido El apellido del estudiante.
     * @param palabraClave Palabra clave para filtrar el contenido de los reportes.
     * @param fecha Fecha para filtrar los reportes.
     * @return Lista de reportes que cumplen con los filtros.
     */
    public List<Reporte> obtenerHistorialReportes(String nombre, String apellido, String palabraClave, String fecha) {
        // Buscar al estudiante por nombre y apellido
        Optional<Estudiante> estudianteOpt = estudianteRepository.findByNombreAndApellido(nombre, apellido);

        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            // Si hay filtros adicionales, aplicar
            if (palabraClave != null && fecha != null) {
                return reporteRepository.findByEstudianteIdAndContenidoContainingIgnoreCaseAndFechaReporte(
                        estudiante.getId(), palabraClave, fecha);
            } else if (palabraClave != null) {
                return reporteRepository.findByEstudianteIdAndContenidoContainingIgnoreCase(estudiante.getId(), palabraClave);
            } else if (fecha != null) {
                return reporteRepository.findByEstudianteIdAndFechaReporte(estudiante.getId(), fecha);
            } else {
                return reporteRepository.findByEstudianteId(estudiante.getId()); // Si no se aplica ningún filtro, devolver todos los reportes
            }
        }
        return null; // Si no se encuentra el estudiante
    }

    // Obtener historial de quejas de un acudiente con filtros opcionales
    /**
     * Obtiene el historial de quejas de un acudiente, con filtros opcionales como
     * palabra clave y fecha.
     *
     * @param nombre El nombre del acudiente.
     * @param apellido El apellido del acudiente.
     * @param palabraClave Palabra clave para filtrar el contenido de las quejas.
     * @param fecha Fecha para filtrar las quejas.
     * @return Lista de quejas que cumplen con los filtros.
     */
    public List<Queja> obtenerHistorialQuejas(String nombre, String apellido, String palabraClave, String fecha) {
        // Buscar al acudiente por nombre y apellido
        Optional<Acudiente> acudienteOpt = acudienteRepository.findByNombreAndApellido(nombre, apellido);

        if (acudienteOpt.isPresent()) {
            Acudiente acudiente = acudienteOpt.get();
            // Si hay filtros adicionales, aplicar
            if (palabraClave != null && fecha != null) {
                return quejaRepository.findByAcudienteIdAndContenidoContainingIgnoreCaseAndFechaQueja(
                        acudiente.getId(), palabraClave, fecha);
            } else if (palabraClave != null) {
                return quejaRepository.findByAcudienteIdAndContenidoContainingIgnoreCase(acudiente.getId(), palabraClave);
            } else if (fecha != null) {
                return quejaRepository.findByAcudienteIdAndFechaQueja(acudiente.getId(), fecha);
            } else {
                return quejaRepository.findByAcudienteId(acudiente.getId()); // Si no se aplica ningún filtro, devolver todas las quejas
            }
        }
        return null; // Si no se encuentra el acudiente
    }

    // Crear grupo
    /**
     * Crea un nuevo grupo asignando estudiantes, un docente y acudientes.
     *
     * @param grupoRequest Objeto GrupoRequest con la información del grupo a crear.
     * @return true si el grupo fue creado correctamente, false si hubo algún problema.
     */
    public boolean crearGrupo(GrupoRequest grupoRequest) {
        Optional<Docente> docenteOpt = docenteRepository.findById(grupoRequest.getDocenteId());
        if (!docenteOpt.isPresent()) {
            return false; // El docente no fue encontrado
        }
        Docente docente = docenteOpt.get();

        // Validar que el docente no esté asignado a otro grupo
        if (grupoRepository.existsByDocenteId(docente.getId())) {
            return false; // El docente ya está asignado a otro grupo
        }

        // Crear el grupo
        Grupo grupo = new Grupo();
        grupo.setNombre(grupoRequest.getNombre());
        grupo.setDocente(docente);

        grupoRepository.save(grupo);

        // Asignar estudiantes al grupo
        for (Long estudianteId : grupoRequest.getEstudiantesIds()) {
            Optional<Estudiante> estudianteOpt = estudianteRepository.findById(estudianteId);
            if (estudianteOpt.isPresent()) {
                Estudiante estudiante = estudianteOpt.get();
                grupo.addEstudiante(estudiante); // Asignar estudiante al grupo
            }
        }

        // Asignar acudientes a los estudiantes
        for (Long acudienteId : grupoRequest.getAcudientesIds()) {
            Optional<Acudiente> acudienteOpt = acudienteRepository.findById(acudienteId);
            if (acudienteOpt.isPresent()) {
                Acudiente acudiente = acudienteOpt.get();
                grupo.addAcudiente(acudiente); // Asignar acudiente al grupo
            }
        }

        grupoRepository.save(grupo);
        return true; // Grupo creado correctamente
    }

    // Enviar notificación a un grupo específico
    /**
     * Envía una notificación a un grupo específico.
     *
     * @param grupoId El ID del grupo al que se enviará la notificación.
     * @param mensaje El mensaje de la notificación.
     * @return true si la notificación fue enviada exitosamente, false si hubo algún problema.
     */
    public boolean enviarNotificacion(Long grupoId, String mensaje) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (!grupoOpt.isPresent()) {
            return false; // No se encontró el grupo
        }
        Grupo grupo = grupoOpt.get();

        // Crear una nueva notificación
        Notificacion notificacion = new Notificacion();
        notificacion.setGrupo(grupo);
        notificacion.setMensaje(mensaje);

        notificacionRepository.save(notificacion);
        return true; // Notificación enviada correctamente
    }
}
