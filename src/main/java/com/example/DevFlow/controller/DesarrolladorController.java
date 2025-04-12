package com.example.DevFlow.controller;

import com.example.DevFlow.model.Desarrollador;
import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.DesarrolladorService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DesarrolladorController {

    @Autowired
    private DesarrolladorService desarrolladorService;

    //-VISTAS-----------------------------------------------------------------------------------------------    
    @GetMapping("/admin/desarrolladores")
    public String listarDesarrolladores(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model, HttpSession session) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        model.addAttribute("nombreUsuario", usuarioSesion.getNombre());

        List<Desarrollador> desarrolladores;

        // Verifica si hay filtros
        boolean hayFiltros = (filtro != null && !filtro.isBlank()) || (estado != null && !estado.isBlank());

        desarrolladores = hayFiltros
                ? desarrolladorService.obtenerDesarrolladoresFiltrados(filtro, estado)
                : desarrolladorService.obtenerTodos();

        // Agrega datos al modelo
        model.addAttribute("desarrolladores", desarrolladores);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "desarrolladores-admin";
    }

    @GetMapping("/admin/desarrolladores/nuevo")
    public String mostrarFormularioNuevoDesarrollador(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        // Agrega el nombre del usuario logueado al modelo
        model.addAttribute("nombreUsuario", usuarioSesion.getNombre());

        // Muestra la vista con el formulario
        return "desarrollador-nuevo";
    }

    @GetMapping("/admin/desarrolladores/editar/{id}")
    public String mostrarFormularioEdicionDesarrollador(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
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

    //-ALTA, BAJA Y MODIFICACION----------------------------------------------------------------------------
    @PostMapping("/admin/desarrolladores/nuevo")
    public String crearDesarrollador(
            @RequestParam String nombre,
            @RequestParam String habilidades,
            HttpSession session,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        Desarrollador nuevo = new Desarrollador(nombre, habilidades);

        desarrolladorService.crearDesarrollador(nuevo);

        return "redirect:/admin/desarrolladores";
    }

    @GetMapping("/admin/desarrolladores/eliminar/{id}")
    public String eliminarDesarrollador(@PathVariable Long id, HttpSession session) {
        desarrolladorService.eliminarDesarrollador(id);

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // Verifica que sea administrador
        String redireccion = verificarQueSeaAdministrador(usuarioSesion);
        if (redireccion != null) {
            return redireccion;
        }

        return "redirect:/admin/desarrolladores";
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

    //-UTILES-----------------------------------------------------------------------------------------------    
    private String verificarQueSeaAdministrador(Usuario usuario) {
        if (usuario == null || usuario.getRol() != RolUsuario.ADMINISTRADOR) {
            return "redirect:/login";
        }
        return null;
    }
}
