package com.proyecto_de_aula.EducationalHarmonie_pa5.Controller;

import com.proyecto_de_aula.EducationalHarmonie_pa5.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin") // Definimos la ruta base para todas las solicitudes
public class AdministradorController {

    // Inyección de dependencias de los servicios necesarios
    @Autowired
    private UsuarioRepository usuarioRepository;  // Repositorio para manejar usuarios
    @Autowired
    private ReporteService reporteService;  // Servicio para manejar reportes
    @Autowired
    private QuejaService quejaService;  // Servicio para manejar quejas
    @Autowired
    private GrupoService grupoService;  // Servicio para manejar grupos
    @Autowired
    private NotificacionService notificacionService;  // Servicio para enviar notificaciones

    // Registrar usuario con validación de datos
    @PostMapping("/usuarios")
    public boolean registrarUsuario(@RequestBody Usuario usuario) {
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

    // Método para verificar si el rol es válido (solo docente o acudiente)
    private boolean esRolValido(String rol) {
        return rol != null && (rol.equals("docente") || rol.equals("acudiente"));
    }

    // Listar usuarios con filtros por rol, nombre o apellido
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido) {

        // Llamar al servicio que maneja la lógica de negocio para obtener los usuarios
        List<Usuario> usuarios = usuarioRepository.listarUsuarios(rol, nombre, apellido);
        return ResponseEntity.ok(usuarios);  // Retorna los usuarios encontrados
    }

    // Obtener un usuario por su ID para editar
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        // Obtener el usuario de la base de datos usando el ID
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Editar un usuario
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<String> editarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        // Llamar al servicio para editar el usuario
        boolean actualizado = usuarioRepository.editarUsuario(id, usuario);
        if (actualizado) {
            return ResponseEntity.ok("Usuario actualizado con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al actualizar el usuario.");
        }
    }

    // Eliminar un usuario
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        // Llamar al servicio para eliminar un usuario
        boolean eliminado = usuarioRepository.eliminarUsuario(id);
        if (eliminado) {
            return ResponseEntity.ok("Usuario eliminado con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al eliminar el usuario.");
        }
    }

    // Obtener historial de reportes de un estudiante con filtros opcionales
    @GetMapping("/reportes")
    public ResponseEntity<List<Reporte>> obtenerHistorialReportes(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam(required = false) String palabraClave,
            @RequestParam(required = false) String fecha) {

        // Obtener los reportes con filtros
        List<Reporte> reportes = reporteService.obtenerHistorialReportes(nombre, apellido, palabraClave, fecha);

        // Verificar si se encontraron reportes y devolverlos
        if (reportes != null && !reportes.isEmpty()) {
            return ResponseEntity.ok(reportes);
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener historial de quejas de un acudiente con filtros opcionales
    @GetMapping("/quejas")
    public ResponseEntity<List<Queja>> obtenerHistorialQuejas(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam(required = false) String palabraClave,
            @RequestParam(required = false) String fecha) {

        // Obtener las quejas con los filtros aplicados
        List<Queja> quejas = quejaService.obtenerHistorialQuejas(nombre, apellido, palabraClave, fecha);

        // Verificar si se encontraron quejas y devolverlas
        if (quejas != null && !quejas.isEmpty()) {
            return ResponseEntity.ok(quejas);
        }
        return ResponseEntity.notFound().build();
    }

    // Crear grupo
    @PostMapping("/grupos")
    public ResponseEntity<String> crearGrupo(@RequestBody GrupoRequest grupoRequest) {
        // Llamar al servicio para crear un grupo
        boolean creado = grupoService.crearGrupo(grupoRequest);
        if (creado) {
            return ResponseEntity.ok("Grupo creado con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al crear el grupo. Verifique los datos del docente.");
        }
    }

    // Listar grupos
    @GetMapping("/grupos")
    public ResponseEntity<List<Grupo>> listarGrupos() {
        // Obtener todos los grupos
        List<Grupo> grupos = grupoService.listarGrupos();
        return ResponseEntity.ok(grupos);  // Retorna los grupos encontrados
    }

    // Obtener grupo por ID
    @GetMapping("/grupos/{id}")
    public ResponseEntity<Grupo> obtenerGrupoPorId(@PathVariable Long id) {
        // Obtener el grupo con el ID especificado
        Optional<Grupo> grupo = grupoService.obtenerGrupoPorId(id);
        return grupo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener grupo por nombre
    @GetMapping("/grupos/nombre/{nombre}")
    public ResponseEntity<Grupo> obtenerGrupoPorNombre(@PathVariable String nombre) {
        // Obtener el grupo por nombre
        Optional<Grupo> grupo = grupoService.obtenerGrupoPorNombre(nombre);
        return grupo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Editar grupo
    @PutMapping("/grupos/{id}")
    public ResponseEntity<String> editarGrupo(@PathVariable Long id, @RequestBody GrupoRequest grupoRequest) {
        // Llamar al servicio para editar el grupo
        boolean actualizado = grupoService.editarGrupo(id, grupoRequest);
        if (actualizado) {
            return ResponseEntity.ok("Grupo actualizado con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al actualizar el grupo. Verifique los datos.");
        }
    }

    // Eliminar grupo
    @DeleteMapping("/grupos/{id}")
    public ResponseEntity<String> eliminarGrupo(@PathVariable Long id) {
        // Llamar al servicio para eliminar el grupo
        boolean eliminado = grupoService.eliminarGrupo(id);
        if (eliminado) {
            return ResponseEntity.ok("Grupo eliminado con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al eliminar el grupo.");
        }
    }

    // Enviar notificación
    @PostMapping("/notificaciones")
    public ResponseEntity<String> enviarNotificacion(@RequestBody NotificacionRequest notificacionRequest) {
        // Llamar al servicio para enviar una notificación
        boolean enviada = notificacionService.enviarNotificacion(notificacionRequest);
        if (enviada) {
            return ResponseEntity.ok("Notificación enviada con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Error al enviar la notificación.");
        }
    }
}
