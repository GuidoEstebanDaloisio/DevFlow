package com.example.DevFlow.controller;

import com.example.DevFlow.model.EstadoProyecto;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.DesarrolladorService;
import com.example.DevFlow.service.ProyectoService;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DesarrolladorService desarrolladorService;

    @GetMapping("/cliente")
    public String verInicioCliente(HttpSession session, 
            Model model){
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esCliente()) {
            return "redirect:/login";
        }
        
        int cantidadProyectosEnRevision = proyectoService.obtenerCantidadProyectosPorEstadoYCliente(usuario.getId(), EstadoProyecto.ESPERANDO_REVISION);
        int cantidadProyectosAprobados = proyectoService.obtenerCantidadProyectosPorEstadoYCliente(usuario.getId(), EstadoProyecto.APROBADO);
        int cantidadProyectosRechazados = proyectoService.obtenerCantidadProyectosPorEstadoYCliente(usuario.getId(), EstadoProyecto.RECHAZADO);
        int cantidadProyectosEnProceso = proyectoService.obtenerCantidadProyectosPorEstadoYCliente(usuario.getId(), EstadoProyecto.EN_PROGRESO);
        int cantidadProyectosFinalizados = proyectoService.obtenerCantidadProyectosPorEstadoYCliente(usuario.getId(), EstadoProyecto.COMPLETADO);
        int cantidadProyectosCancelados = proyectoService.obtenerCantidadProyectosPorEstadoYCliente(usuario.getId(), EstadoProyecto.CANCELADO);        
        int cantidadProyectosEnPausa = proyectoService.obtenerCantidadProyectosPorEstadoYCliente(usuario.getId(), EstadoProyecto.EN_PAUSA);
        
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("CantProyectosEnRevision", cantidadProyectosEnRevision);
        model.addAttribute("CantProyectosAprobados", cantidadProyectosAprobados);
        model.addAttribute("CantProyectosRechazados", cantidadProyectosRechazados);
        model.addAttribute("CantProyectosEnProceso", cantidadProyectosEnProceso);
        model.addAttribute("CantProyectosFinalizados", cantidadProyectosFinalizados);
        model.addAttribute("CantProyectosCancelados", cantidadProyectosCancelados);
        model.addAttribute("CantProyectosEnPausa", cantidadProyectosEnPausa);

        return "cliente/inicio";
    }

    @GetMapping("/gerente")
    public String verInicioGerente(HttpSession session, 
            Model model){
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }

        int cantidadProyectosEnRevision = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.ESPERANDO_REVISION);
        int cantidadProyectosAprobados = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.APROBADO);
        int cantidadProyectosRechazados = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.RECHAZADO);
        int cantidadProyectosEnProceso = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.EN_PROGRESO);
        int cantidadProyectosFinalizados = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.COMPLETADO);
        int cantidadProyectosCancelados = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.CANCELADO);        
        int cantidadProyectosEnPausa = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.EN_PAUSA);

        model.addAttribute("usuario", usuario);
        model.addAttribute("CantProyectosEnRevision", cantidadProyectosEnRevision);
        model.addAttribute("CantProyectosAprobados", cantidadProyectosAprobados);
        model.addAttribute("CantProyectosRechazados", cantidadProyectosRechazados);
        model.addAttribute("CantProyectosEnProceso", cantidadProyectosEnProceso);
        model.addAttribute("CantProyectosFinalizados", cantidadProyectosFinalizados);
        model.addAttribute("CantProyectosCancelados", cantidadProyectosCancelados);
        model.addAttribute("CantProyectosEnPausa", cantidadProyectosEnPausa);
        
        return "gerente/inicio";
    }

    @GetMapping("/admin")
    public String verInicioAdmin(HttpSession session, 
            Model model){
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        int cantidadUsuarios = usuarioService.obtenerCantidadUsuarios();
        int cantidadProyectosEnProceso = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.EN_PROGRESO);
        int cantidadProyectosEnPausa = proyectoService.obtenerCantidadProyectosPorEstado(EstadoProyecto.EN_PAUSA);
        int cantidadDesarrolladoresDisponibles = desarrolladorService.obtenerCantidadDesarrolladoresDisponibles();

        model.addAttribute("usuario", usuario);
        model.addAttribute("CantUsuarios", cantidadUsuarios);
        model.addAttribute("CantProyectosEnProceso", cantidadProyectosEnProceso);
        model.addAttribute("CantProyectosEnPausa", cantidadProyectosEnPausa);
        model.addAttribute("CantDevDisponibles", cantidadDesarrolladoresDisponibles);

        return "administrador/inicio";
    }
}
