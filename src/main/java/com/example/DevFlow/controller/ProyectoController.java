package com.example.DevFlow.controller;

import com.example.DevFlow.model.Desarrollador;
import com.example.DevFlow.model.EstadoProyecto;
import com.example.DevFlow.model.Proyecto;
import com.example.DevFlow.model.RolUsuario;
import static com.example.DevFlow.model.RolUsuario.*;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.DesarrolladorService;
import com.example.DevFlow.service.ProyectoService;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DesarrolladorService desarrolladorService;

    //-VISTAS ADMINISTRADOR---------------------------------------------------------------------------------    
    @GetMapping("/admin/proyectos")
    public String verProyectosComoAdmin(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        List<Proyecto> proyectos = proyectoService.obtenerListadoDeProyectos(filtro, estado);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "administrador/listadoDeProyectos";
    }

    @GetMapping("/admin/proyectos/detalles/{id}")
    public String verDetallesProyectoComoAdmin(
            @PathVariable Long id,
            Model model,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        Proyecto proyecto = proyectoService.obtenerProyectoPorId(id);
        List<Desarrollador> desarrolladoresAsignados = desarrolladorService.obtenerPorProyecto(proyecto);
        List<Desarrollador> desarrolladoresDisponibles = desarrolladorService.obtenerDesarrolladoresDisponibles();

        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("desarrolladoresAsignados", desarrolladoresAsignados);
        model.addAttribute("desarrolladoresDisponibles", desarrolladoresDisponibles);

        return "administrador/detallesProyecto";
    }

    //-VISTAS CLIENTE---------------------------------------------------------------------------------------    
    @GetMapping("/cliente/proyectos")
    public String verProyectosComoCliente(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esCliente()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        List<Proyecto> proyectos = proyectoService.obtenerListadoDeProyectosParaCliente(usuario.getId(), filtro, estado);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "cliente/listadoDeProyectos";
    }

    @GetMapping("/cliente/proyectos/detalles/{id}")
    public String verDetallesProyectoComoCliente(
            @PathVariable Long id,
            Model model,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esCliente()) {
            return "redirect:/login";
        }

        Proyecto proyecto = proyectoService.obtenerProyectoPorId(id);
        List<Desarrollador> desarrolladoresAsignados = desarrolladorService.obtenerPorProyecto(proyecto);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("desarrolladoresAsignados", desarrolladoresAsignados);

        return "cliente/detallesProyecto";
    }

    //-VISTAS GERENTE---------------------------------------------------------------------------------------    
    @GetMapping("/gerente/proyectos")
    public String verProyectosComoGerente(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String estado,
            Model model,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }

        List<Proyecto> proyectos = proyectoService.obtenerListadoDeProyectos(filtro, estado);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("filtro", filtro);
        model.addAttribute("estadoSeleccionado", estado);

        return "gerente/listadoDeProyectos";
    }

    @GetMapping("/gerente/proyectos/nuevo")
    public String mostrarFormularioNuevoProyecto(
            HttpSession session,
            Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }

        List<Usuario> clientes = usuarioService.obtenerUsuariosPorRol(CLIENTE);
        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("clientes", clientes);

        return "gerente/nuevoProyecto";
    }

    @GetMapping("/gerente/proyectos/detalles/{id}")
    public String verDetallesProyectoComoGerente(
            @PathVariable Long id,
            Model model,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }

        Proyecto proyecto = proyectoService.obtenerProyectoPorId(id);
        List<Desarrollador> desarrolladoresAsignados = desarrolladorService.obtenerPorProyecto(proyecto);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("desarrolladoresAsignados", desarrolladoresAsignados);

        return "gerente/detallesProyecto";
    }

    @GetMapping("/gerente/proyectos/editar/{id}")
    public String mostrarFormularioEditarProyecto(
            @PathVariable Long id,
            Model model,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }

        Proyecto proyecto = proyectoService.obtenerProyectoPorId(id);

        model.addAttribute("nombreUsuario", usuario.getNombre());

        try {
            proyectoService.consultarSiEsPosibleEditarElProyecto(proyecto);

            model.addAttribute("proyecto", proyecto);

            return "gerente/editarProyecto";
        } catch (IllegalArgumentException e) {
            return "redirect:/gerente/proyectos/detalles/" + id + "?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

    }

    //-ALTA, BAJA Y MODIFICACION----------------------------------------------------------------------------
    @PostMapping("/gerente/proyectos/nuevo")
    public String crearProyecto(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String medio_encargo,
            @RequestParam Double presupuesto,
            @RequestParam Long clienteId,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        try {
            Usuario cliente = usuarioService.obtenerUsuarioPorId(clienteId);

            proyectoService.crearProyecto(titulo, descripcion, medio_encargo, presupuesto, cliente);
            return "redirect:/gerente/proyectos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("titulo", titulo);
            model.addAttribute("descripcion", descripcion);
            model.addAttribute("medio_encargo", medio_encargo);
            model.addAttribute("presupuesto", presupuesto);

            // Volvemos a cargar la lista de clientes para el select del formulario
            List<Usuario> clientes = usuarioService.obtenerUsuariosPorRol(CLIENTE);
            model.addAttribute("clientes", clientes);

            return "gerente/nuevoProyecto";
        }
    }

    @PostMapping("/gerente/proyectos/detalles/{id}")
    public String cambiarEstadoProyecto(
            @PathVariable Long id,
            @RequestParam EstadoProyecto nuevoEstado,
            Model model,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }

        try {
            Proyecto proyecto = proyectoService.obtenerProyectoPorId(id);
            proyectoService.cambiarEstado(proyecto, nuevoEstado);
            return "redirect:/gerente/proyectos/detalles/{id}";
        } catch (IllegalArgumentException e) {
            return "redirect:/gerente/proyectos/detalles/" + id + "?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/gerente/proyectos/detalles/{id}/guardar-fecha")
    public String guardarFechaInicio(
            @PathVariable Long id,
            @RequestParam("fechaInicio")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio) {

        // 1) Obtengo el proyecto
        Proyecto proyecto = proyectoService.obtenerProyectoPorId(id);

        // 2) Guardo la fecha de inicio
        proyectoService.establecerFechaInicio(id, fechaInicio);

        // 3) Cambio el estado a EN_PROGRESO
        proyectoService.cambiarEstado(proyecto, EstadoProyecto.EN_PROGRESO);

        // 4) Redirijo a la página de detalles
        return "redirect:/gerente/proyectos/detalles/" + id;
    }

    @PostMapping("/gerente/proyectos/detalles/{id}/guardar-fecha-final")
    public String guardarFechaFin(
            @PathVariable Long id,
            @RequestParam("fechaFin")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {

        // 1) Obtengo el proyecto
        Proyecto proyecto = proyectoService.obtenerProyectoPorId(id);

        // 2) Guardo la fecha de fin
        proyectoService.establecerFechaFin(id, fechaFin);

        // 3) Cambio el estado a COMPLETADO
        proyectoService.cambiarEstado(proyecto, EstadoProyecto.COMPLETADO);

        // 4) Redirijo a la página de detalles
        return "redirect:/gerente/proyectos/detalles/" + id;
    }

    @GetMapping("/gerente/proyectos/eliminar/{id}")
    public String eliminarProyecto(
            @PathVariable Long id,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }

        proyectoService.eliminarProyecto(id);

        return "redirect:/gerente/proyectos";
    }

    @PostMapping("/gerente/proyectos/editar/{id}")
    public String editarProyecto(
            @PathVariable Long id,
            @ModelAttribute Proyecto proyectoActualizado,
            Model model,
            HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        try {
            proyectoService.actualizarProyecto(id, proyectoActualizado);
            return "redirect:/gerente/proyectos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("proyecto", proyectoActualizado);
            return "gerente/editarProyecto";
        }
    }

}
