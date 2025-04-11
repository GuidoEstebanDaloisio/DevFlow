package com.example.DevFlow.controller;

import com.example.DevFlow.model.Desarrollador;
import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.DesarrolladorService;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DesarrolladorService desarrolladorService;

    @GetMapping("/admin")
    public String vistaAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login"; // Si no hay sesión, redirige
        }

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "admin";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    //-SECCION PROYECTOS------------------------------------------------------------------------------------
    @GetMapping("/admin/proyectos")
    public String vistaProyectos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        verificarQueSeaAdministrador(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "proyectos-admin";
    }

    @GetMapping("/admin/proyectos/detalle")
    public String vistaDetalleDeProyecto(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        verificarQueSeaAdministrador(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "detalle-proyecto-admin";
    }

    //-SECCION DESARROLLADORES------------------------------------------------------------------------------
    @GetMapping(value = "/admin/desarrolladores", params = "!filtro")
    public String vistaDesarrolladores(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        verificarQueSeaAdministrador(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("desarrolladores", desarrolladorService.obtenerTodos());

        return "desarrolladores-admin";
    }

    @GetMapping("/admin/desarrolladores")
    public String listarDesarrolladores(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model, HttpSession session) {

        // Verifica que el usuario sea administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        verificarQueSeaAdministrador(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre());

        List<Desarrollador> desarrolladores;

        // Aplica filtros si hay alguno
        if ((filtro != null && !filtro.isBlank()) || (estado != null && !estado.isBlank())) {
            desarrolladores = desarrolladorService.obtenerDesarrolladoresFiltrados(filtro, estado);
        } else {
            desarrolladores = desarrolladorService.obtenerTodos();
        }

        model.addAttribute("desarrolladores", desarrolladores);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "desarrolladores-admin";
    }

    @GetMapping("/admin/desarrolladores/nuevo")
    public String mostrarFormularioNuevoDesarrollador(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        verificarQueSeaAdministrador(usuario);

        // Agrega el nombre del usuario logueado al modelo
        model.addAttribute("nombreUsuario", usuario.getNombre());

        // Muestra la vista con el formulario
        return "desarrollador-nuevo";
    }

    @PostMapping("/admin/desarrolladores/nuevo")
    public String crearDesarrollador(
            @RequestParam String nombre,
            @RequestParam String habilidades,
            Model model) {

        Desarrollador nuevo = new Desarrollador(nombre, habilidades);

        desarrolladorService.crearDesarrollador(nuevo);

        return "redirect:/admin/desarrolladores";
    }

    @GetMapping("/admin/desarrolladores/eliminar/{id}")
    public String eliminarDesarrollador(@PathVariable Long id) {
        desarrolladorService.eliminarDesarrollador(id);
        return "redirect:/admin/desarrolladores";
    }

    
@GetMapping("/admin/desarrolladores/editar/{id}")
public String mostrarFormularioEdicionDesarrollador(@PathVariable Long id, HttpSession session, Model model) {
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

    String redireccion = verificarQueSeaAdministrador(usuarioSesion);
    if (redireccion != null) {
        return redireccion;
    }

    Optional<Desarrollador> desarrolladorOptional = desarrolladorService.obtenerDesarrolladorPorId(id);
    if (desarrolladorOptional.isEmpty()) {
        return "redirect:/admin/desarrolladores?error=No se encontró el desarrollador";
    }

    model.addAttribute("desarrollador", desarrolladorOptional.get());
    return "formulario-editar-desarrollador-admin";
}



@PostMapping("/admin/desarrolladores/editar/{id}")
public String actualizarDesarrollador(@PathVariable Long id,
        @ModelAttribute Desarrollador desarrolladorActualizado,
        HttpSession session,
        Model model) {

    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

    // Verifica que sea administrador
    String redireccion = verificarQueSeaAdministrador(usuarioSesion);
    if (redireccion != null) {
        return redireccion;
    }

    try {
        desarrolladorService.actualizarNombreYHabilidades(id, desarrolladorActualizado.getNombre(), desarrolladorActualizado.getHabilidades());
        return "redirect:/admin/desarrolladores";
    } catch (IllegalArgumentException e) {
        model.addAttribute("desarrollador", desarrolladorActualizado);
        model.addAttribute("error", e.getMessage());
        return "formulario-editar-desarrollador-admin";
    }
}

    //---------------- SECCIÓN USUARIOS ------------------------------------------------------------------------
    /**
     * Muestra la vista de usuarios sin filtro. Se ejecuta solo si no hay
     * parámetro 'filtro' en la URL.
     */
    @GetMapping(value = "/admin/usuarios", params = "!filtro")
    public String vistaUsuarios(HttpSession session, Model model) {
        // Obtiene el usuario logueado desde la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verifica que sea un administrador
        verificarQueSeaAdministrador(usuario);

        // Agrega al modelo el nombre y todos los usuarios
        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());

        // Devuelve la vista con la lista de usuarios
        return "usuarios-admin";
    }

    /**
     * Muestra la lista de usuarios, aplicando filtros si están presentes. Se
     * ejecuta cuando hay parámetros 'filtro' o 'rol', aunque estén vacíos.
     */
    @GetMapping("/admin/usuarios")
    public String listarUsuarios(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String rol,
            Model model, HttpSession session) {

        // Verifica que el usuario logueado sea administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        verificarQueSeaAdministrador(usuario);

        // Agrega el nombre al modelo
        model.addAttribute("nombreUsuario", usuario.getNombre());

        List<Usuario> usuarios;

        // Aplica filtros si están presentes
        if ((filtro != null && !filtro.isBlank()) || (rol != null && !rol.isBlank())) {
            usuarios = usuarioService.obtenerUsuariosFiltrados(filtro, rol);
        } else {
            usuarios = usuarioService.obtenerUsuarios();
        }

        // Agrega los datos al modelo para mantenerlos en el formulario
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", filtro);
        model.addAttribute("rolSeleccionado", rol);

        return "usuarios-admin";
    }

    /**
     * Muestra el formulario para crear un nuevo usuario.
     */
    @GetMapping("/admin/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        verificarQueSeaAdministrador(usuario);

        // Agrega el nombre del usuario logueado al modelo
        model.addAttribute("nombreUsuario", usuario.getNombre());

        // Muestra la vista con el formulario
        return "usuario-nuevo";
    }

    /**
     * Procesa el formulario para crear un nuevo usuario.
     */
    @PostMapping("/admin/usuarios/nuevo")
    public String crearUsuario(@RequestParam String nombre,
            @RequestParam String email,
            @RequestParam Long telefono,
            @RequestParam RolUsuario rol,
            @RequestParam String contrasenia,
            Model model) {

        // Verifica si ya existe un usuario con el mismo email
        if (usuarioService.obtenerUsuarioPorEmail(email) != null) {
            model.addAttribute("error", "Ya existe un usuario con ese email.");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            model.addAttribute("telefono", telefono);
            model.addAttribute("rol", rol);
            model.addAttribute("contrasenia", contrasenia);
            return "usuario-nuevo";
        }

        // Verifica si ya existe un usuario con el mismo nombre y contraseña
        boolean existeNombreYContrasenia = usuarioService.obtenerUsuarios().stream()
                .anyMatch(u -> u.getNombre().equalsIgnoreCase(nombre) && u.getContrasenia().equals(contrasenia));

        if (existeNombreYContrasenia) {
            model.addAttribute("error", "Ya existe un usuario con ese nombre y contraseña.");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            model.addAttribute("telefono", telefono);
            model.addAttribute("rol", rol);
            model.addAttribute("contrasenia", contrasenia);
            return "usuario-nuevo";
        }

        // Crea el nuevo usuario y lo guarda
        Usuario nuevo = new Usuario(nombre, contrasenia, email, telefono, rol);
        usuarioService.crearUsuario(nuevo);

        // Redirige a la vista de usuarios
        return "redirect:/admin/usuarios";
    }

    /**
     * Elimina un usuario por ID.
     */
    @GetMapping("/admin/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/admin/usuarios";
    }

    /**
     * Muestra el formulario para editar un usuario específico.
     */
    @GetMapping("/admin/usuarios/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        // Obtiene el usuario a editar y lo pasa a la vista
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        model.addAttribute("usuario", usuario);
        return "formulario-editar-usuario-admin";
    }

    /**
     * Procesa la actualización de un usuario.
     */
    @PostMapping("/admin/usuarios/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
            @ModelAttribute Usuario usuarioActualizado,
            HttpSession session,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        // Verifica si el email ya está en uso por otro usuario
        Usuario usuarioExistente = usuarioService.obtenerUsuarioPorEmail(usuarioActualizado.getEmail());
        if (usuarioExistente != null && !usuarioExistente.getId().equals(id)) {
            model.addAttribute("usuario", usuarioActualizado);
            model.addAttribute("error", "El email ya está siendo utilizado por otro usuario.");
            return "formulario-editar-usuario-admin";
        }

        // Intenta actualizar el usuario
        try {
            usuarioService.actualizarUsuario(id, usuarioActualizado);
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuarioActualizado);
            model.addAttribute("error", e.getMessage());
            return "formulario-editar-usuario-admin";
        }
    }

    //-UTILES-----------------------------------------------------------------------------------------------    
    private String verificarQueSeaAdministrador(Usuario usuario) {
        if (usuario == null || usuario.getRol() != RolUsuario.ADMINISTRADOR) {
            return "redirect:/login";
        }
        return null;
    }

}
