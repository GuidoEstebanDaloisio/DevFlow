package com.example.DevFlow.controller;

import com.example.DevFlow.model.Desarrollador;
import com.example.DevFlow.model.Proyecto;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.DesarrolladorService;
import com.example.DevFlow.service.ProyectoService;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DesarrolladorController {

    @Autowired
    private DesarrolladorService desarrolladorService;

    @Autowired
    private ProyectoService proyectoService;

    //-VISTAS-----------------------------------------------------------------------------------------------    
    @GetMapping("/admin/desarrolladores")
    public String verDesarrolladores(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        model.addAttribute("nombreUsuario", usuario.getNombre());

        List<Desarrollador> desarrolladores = desarrolladorService.obtenerListadoDeDesarrolladores(filtro, estado);

        // Agrega datos al modelo
        model.addAttribute("desarrolladores", desarrolladores);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "administrador/listadoDeDesarrolladores";
    }

    @GetMapping("/admin/desarrolladores/nuevo")
    public String mostrarFormularioNuevoDesarrollador(HttpSession session,
            Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        // Muestra la vista con el formulario
        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "administrador/nuevoDesarrollador";
    }

    @GetMapping("/admin/desarrolladores/editar/{id}")
    public String mostrarFormularioEdicionDesarrollador(@PathVariable Long id,
            HttpSession session,
            Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        try {
            Desarrollador desarrollador = desarrolladorService.obtenerDesarrolladorPorId(id);
            model.addAttribute("desarrollador", desarrollador);
            return "administrador/editarDesarrollador";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/desarrolladores?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);    //Envio el error desde el servicio
        }
    }

    //-ASIGNACION Y DESASIGNACION---------------------------------------------------------------------------
    @PostMapping("/admin/asignarDesarrollador")
    public String asignarDesarrollador(@RequestParam Long proyectoId,
            HttpSession session,
            @RequestParam Long desarrolladorId) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        try {
            desarrolladorService.asignarAProyecto(proyectoId, desarrolladorId);
            return ("redirect:/admin/proyectos/detalles/" + proyectoId);
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/proyectos/detalles/{proyectoId}?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/admin/desasignarDesarrollador")
    public String desasignarDesarrollador(@RequestParam Long proyectoId,
            HttpSession session,
            @RequestParam Long desarrolladorId) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        try {
            desarrolladorService.desasignarAProyecto(proyectoId, desarrolladorId);
            return ("redirect:/admin/proyectos/detalles/" + proyectoId);
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/proyectos/detalles/{proyectoId}?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    //-ALTA, BAJA Y MODIFICACION----------------------------------------------------------------------------
    @PostMapping("/admin/desarrolladores/nuevo")
    public String crearDesarrollador(
            @RequestParam String nombre,
            @RequestParam String habilidades,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        desarrolladorService.crearDesarrollador(nombre, habilidades);

        return "redirect:/admin/desarrolladores";
    }

    @GetMapping("/admin/desarrolladores/eliminar/{id}")
    public String eliminarDesarrollador(@PathVariable Long id,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }
        desarrolladorService.eliminarDesarrollador(id);
        return "redirect:/admin/desarrolladores";
    }

    @PostMapping("/admin/desarrolladores/editar/{id}")
    public String actualizarDesarrollador(@PathVariable Long id,
            @ModelAttribute Desarrollador desarrolladorActualizado,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        try {
            desarrolladorService.actualizarNombreYHabilidades(id, desarrolladorActualizado.getNombre(), desarrolladorActualizado.getHabilidades());
            return "redirect:/admin/desarrolladores";
        } catch (IllegalArgumentException e) {
            model.addAttribute("desarrollador", desarrolladorActualizado);
            model.addAttribute("error", e.getMessage());
            return "administrador/editarDesarrollador";
        }
    }
}
