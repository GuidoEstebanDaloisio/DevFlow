package com.example.DevFlow.controller;

import com.example.DevFlow.model.Desarrollador;
import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.DesarrolladorService;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public String verDesarrolladores(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }

        model.addAttribute("nombreUsuario", usuario.getNombre());

        List<Desarrollador> desarrolladores;

        // Verifica si hay filtros
        boolean hayFiltros = (filtro != null && !filtro.isBlank()) || (estado != null && !estado.isBlank());

        desarrolladores = hayFiltros
                ? desarrolladorService.obtenerDesarrolladoresFiltrados(filtro, estado)
                : desarrolladorService.obtenerDesarrolladores();

        // Agrega datos al modelo
        model.addAttribute("desarrolladores", desarrolladores);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "administrador/listadoDeDesarrolladores";
    }

    @GetMapping("/admin/desarrolladores/nuevo")
    public String mostrarFormularioNuevoDesarrollador(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }

        // Muestra la vista con el formulario
        return "administrador/nuevoDesarrollador";
    }

    @GetMapping("/admin/desarrolladores/editar/{id}")
    public String mostrarFormularioEdicionDesarrollador(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }

        try {
            Desarrollador desarrollador = desarrolladorService.obtenerDesarrolladorPorId(id);
            model.addAttribute("desarrollador", desarrollador);
            return "administrador/editarDesarrollador";
        } catch (IllegalArgumentException e) {
        return "redirect:/admin/desarrolladores?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);    //Envio el error desde el servicio
        }
    }

    //-ALTA, BAJA Y MODIFICACION----------------------------------------------------------------------------
    @PostMapping("/admin/desarrolladores/nuevo")
    public String crearDesarrollador(
            @RequestParam String nombre,
            @RequestParam String habilidades,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }
        Desarrollador nuevo = new Desarrollador(nombre, habilidades);

        desarrolladorService.crearDesarrollador(nuevo);

        return "redirect:/admin/desarrolladores";
    }

    @GetMapping("/admin/desarrolladores/eliminar/{id}")
    public String eliminarDesarrollador(@PathVariable Long id, HttpSession session) {
        desarrolladorService.eliminarDesarrollador(id);

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }

        return "redirect:/admin/desarrolladores";
    }

    @PostMapping("/admin/desarrolladores/editar/{id}")
    public String actualizarDesarrollador(@PathVariable Long id,
            @ModelAttribute Desarrollador desarrolladorActualizado,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }

        try {
            desarrolladorService.actualizarNombreYHabilidades(id, desarrolladorActualizado.getNombre(), desarrolladorActualizado.getHabilidades());
            return "redirect:/admin/desarrolladores";
        } catch (IllegalArgumentException e) {
            model.addAttribute("desarrollador", desarrolladorActualizado);
            model.addAttribute("error", e.getMessage());
            return "administrador/editarDesarrollador";
        }
    }

    //-UTILES-----------------------------------------------------------------------------------------------    
    private boolean noEsAdministrador(Usuario usuario) {
        return usuario == null || usuario.getRol() != RolUsuario.ADMINISTRADOR;
    }
}
