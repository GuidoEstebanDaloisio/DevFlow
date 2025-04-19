package com.example.DevFlow.controller;

import com.example.DevFlow.model.EstadoProyecto;
import com.example.DevFlow.model.Proyecto;
import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
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
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    //-VISTAS ADMINISTRADOR---------------------------------------------------------------------------------    
    @GetMapping("/admin/proyectos")
    public String verProyectosComoAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());   //Se pasa el nombre del usuario logueado para mostrarlo en la vista

        return "administrador/listadoDeProyectos";
    }

    @GetMapping("/admin/proyectos/detalle")
    public String verDetalleDeProyectoComoAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsAdministrador(usuario)) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        return "administrador/detalleProyecto";
    }

    //-VISTAS CLIENTE---------------------------------------------------------------------------------------    
    @GetMapping("/cliente/proyectos")
    public String verProyectosComoCliente(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsCliente(usuario)) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        List<Proyecto> proyectos;

        if ((filtro != null && !filtro.isBlank()) || (estado != null && !estado.isBlank())) {
            proyectos = proyectoService.obtenerProyectosFiltradosParaCliente(usuario.getId(), filtro, estado);
        } else {
            proyectos = proyectoService.obtenerProyectosPorIdCliente(usuario.getId());
        }

        model.addAttribute("proyectos", proyectos);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "cliente/listadoDeProyectos";
    }

    @GetMapping("/cliente/proyectos/nuevo")
    public String mostrarFormularioNuevoProyecto(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsCliente(usuario)) {
            return "redirect:/login";
        }

        return "cliente/nuevoProyecto";
    }

    @GetMapping("/cliente/proyectos/editar/{id}")
    public String mostrarFormularioEdicionCliente(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsCliente(usuario)) {
            return "redirect:/login";
        }

        try {
            Proyecto proyecto = proyectoService.obtenerProyectoParaEdicionPorCliente(id, usuario);
            model.addAttribute("proyecto", proyecto);
            return "formulario-editar-proyecto-cliente";
        } catch (IllegalArgumentException e) {
            return "redirect:/cliente/proyectos?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);    //Envio el error desde el servicio
        }
    }

    //-ALTA, BAJA Y MODIFICACION----------------------------------------------------------------------------
    @PostMapping("/cliente/proyectos/nuevo")
    public String crearProyecto(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String medio_encargo,
            @RequestParam Double presupuesto,
            HttpSession session,
            Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsCliente(usuario)) {
            return "redirect:/login";
        }

        try {
            Proyecto nuevo = new Proyecto(titulo, descripcion, medio_encargo, presupuesto, usuario);
            proyectoService.crearProyecto(nuevo);
            return "redirect:/cliente/proyectos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("titulo", titulo);
            model.addAttribute("descripcion", descripcion);
            model.addAttribute("medio_encargo", medio_encargo);
            model.addAttribute("presupuesto", presupuesto);
            return "cliente/nuevoProyecto";
        }

    }

    @PostMapping("/cliente/proyectos/editar/{id}")
    public String actualizarProyectoComoCliente(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String medioEncargo,
            @RequestParam Double presupuesto,
            HttpSession session,
            Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (noEsCliente(usuario)) {
            return "redirect:/login";
        }

        try {
            proyectoService.actualizarProyectoComoCliente(id, usuario, titulo, descripcion, medioEncargo, presupuesto);
            return "redirect:/cliente/proyectos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombreUsuario", usuario.getNombre());
            model.addAttribute("proyecto", proyectoService.obtenerProyectoPorId(id));
            return "cliente/editarProyecto";
        }
    }

    //-UTILES-----------------------------------------------------------------------------------------------    
    private boolean noEsCliente(Usuario usuario) {
        return usuario == null || usuario.getRol() != RolUsuario.CLIENTE;
    }

    private boolean noEsAdministrador(Usuario usuario) {
        return usuario == null || usuario.getRol() != RolUsuario.ADMINISTRADOR;
    }
}
