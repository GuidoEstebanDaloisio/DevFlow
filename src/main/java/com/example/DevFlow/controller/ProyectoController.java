package com.example.DevFlow.controller;

import com.example.DevFlow.model.EstadoProyecto;
import com.example.DevFlow.model.Proyecto;
import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.ProyectoService;
import jakarta.servlet.http.HttpSession;
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
    public String vistaProyectos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        verificarQueSeaAdministrador(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "administrador/listadoDeProyectos";
    }

    @GetMapping("/admin/proyectos/detalle")
    public String vistaDetalleDeProyecto(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        verificarQueSeaAdministrador(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "administrador/detalleProyecto";
    }

    //-VISTAS CLIENTE---------------------------------------------------------------------------------------    
    @GetMapping("/cliente/proyectos")
    public String verProyectosComoCliente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null || usuario.getRol() != RolUsuario.CLIENTE) {
            return "redirect:/login";
        }

        model.addAttribute("nombreUsuario", usuario.getNombre());

        // Supone que tenés un servicio que trae los proyectos del cliente actual
        List<Proyecto> proyectosCliente = proyectoService.obtenerProyectosPorIdCliente(usuario.getId());
        model.addAttribute("proyectos", proyectosCliente);

        return "cliente/listadoDeProyectos";
    }

    @GetMapping("/cliente/proyectos/nuevo")
    public String mostrarFormularioNuevoProyecto(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verifica que sea cliente
        String redireccion = verificarQueSeaCliente(usuario);
        if (redireccion != null) {
            return redireccion;
        }

        // Agrega nombre del cliente a la vista
        model.addAttribute("nombreUsuario", usuario.getNombre());

        return "cliente/nuevoProyecto";
    }

    @GetMapping("/cliente/proyectos/editar/{id}")
    public String mostrarFormularioEdicionCliente(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        String redireccion = verificarQueSeaCliente(usuario);
        if (redireccion != null) {
            return redireccion;
        }

        try {
            Proyecto proyecto = proyectoService.validarEdicionProyectoPorCliente(id, usuario); //Se vuelve a llamar a "validarEdicionProyectoPorCliente" al enviar el formulario, porque en una aplicación web no se puede confiar en los datos del cliente (puede modificar el id en la URL, o incluso forzar un POST con un proyecto que no le pertenece)
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("nombreUsuario", usuario.getNombre());
            return "formulario-editar-proyecto-cliente";
        } catch (IllegalArgumentException e) {
            return "redirect:/cliente/proyectos";
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

        verificarQueSeaCliente(usuario);

        Proyecto nuevo = new Proyecto(titulo, descripcion, medio_encargo, presupuesto, usuario);
        proyectoService.crearProyecto(nuevo);

        return "redirect:/cliente/proyectos";
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

        String redireccion = verificarQueSeaCliente(usuario);
        if (redireccion != null) {
            return redireccion;
        }

        try {
            proyectoService.actualizarProyectoCliente(id, usuario, titulo, descripcion, medioEncargo, presupuesto);
            return "redirect:/cliente/proyectos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombreUsuario", usuario.getNombre());
            model.addAttribute("proyecto", proyectoService.obtenerProyectoPorId(id));
            return "cliente/editarProyecto";
        }
    }

    //-UTILES-----------------------------------------------------------------------------------------------    
    private String verificarQueSeaCliente(Usuario usuario) {
        if (usuario == null || usuario.getRol() != RolUsuario.CLIENTE) {
            return "redirect:/login";
        }
        return null;
    }

    private String verificarQueSeaAdministrador(Usuario usuario) {
        if (usuario == null || usuario.getRol() != RolUsuario.ADMINISTRADOR) {
            return "redirect:/login";
        }
        return null;
    }
}
