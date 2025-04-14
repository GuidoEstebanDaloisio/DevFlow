package com.example.DevFlow.controller;

import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //-VISTAS CLIENTE---------------------------------------------------------------------------------------    
    @GetMapping("/cliente")
    public String vistaCliente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login"; // Si no hay sesión, redirige
        }

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "cliente/inicio";
    }

    //-VISTAS ADMINISTRADOR---------------------------------------------------------------------------------    
    @GetMapping("/admin")
    public String vistaAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login"; // Si no hay sesión, redirige
        }

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "administrador/inicio";
    }

    @GetMapping("/admin/usuarios")
    public String verUsuariosComoAdmin(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String rol,
            Model model, HttpSession session) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");


        // Agrega el nombre al modelo
        model.addAttribute("nombreUsuario", usuarioSesion.getNombre());

        List<Usuario> usuarios;

        // Si hay filtros, aplica; sino, trae todo
        if ((filtro != null && !filtro.isBlank()) || (rol != null && !rol.isBlank())) {
            usuarios = usuarioService.obtenerUsuariosFiltrados(filtro, rol);
        } else {
            usuarios = usuarioService.obtenerUsuarios();
        }

        // Agrega los datos al modelo
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", filtro);
        model.addAttribute("rolSeleccionado", rol);

        return "administrador/listadoDeUsuarios";
    }

    @GetMapping("/admin/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(HttpSession session, Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        // Agrega el nombre del usuario logueado al modelo
        model.addAttribute("nombreUsuario", usuarioSesion.getNombre());

        // Muestra la vista con el formulario
        return "administrador/nuevoUsuario";
    }

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
        return "administrador/editarUsuario";
    }

    //-ALTA, BAJA Y MODIFICACION----------------------------------------------------------------------------
    @PostMapping("/admin/usuarios/nuevo")
    public String crearUsuario(@RequestParam String nombre,
            @RequestParam String email,
            @RequestParam Long telefono,
            @RequestParam RolUsuario rol,
            @RequestParam String contrasenia,
            HttpSession session,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        // Verifica si ya existe un usuario con el mismo email
        if (usuarioService.obtenerUsuarioPorEmail(email) != null) {
            model.addAttribute("error", "Ya existe un usuario con ese email.");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            model.addAttribute("telefono", telefono);
            model.addAttribute("rol", rol);
            model.addAttribute("contrasenia", contrasenia);
            return "administrador/nuevoUsuario";
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
            return "administrador/nuevoUsuario";
        }

        // Crea el nuevo usuario y lo guarda
        Usuario nuevo = new Usuario(nombre, contrasenia, email, telefono, rol);
        usuarioService.crearUsuario(nuevo);

        // Redirige a la vista de usuarios
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, HttpSession session) {
        usuarioService.eliminarUsuario(id);

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        return "redirect:/admin/usuarios";
    }

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
            return "administrador/editarUsuario";
        }

        // Intenta actualizar el usuario
        try {
            usuarioService.actualizarUsuario(id, usuarioActualizado);
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuarioActualizado);
            model.addAttribute("error", e.getMessage());
            return "administrador/editarUsuario";
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
