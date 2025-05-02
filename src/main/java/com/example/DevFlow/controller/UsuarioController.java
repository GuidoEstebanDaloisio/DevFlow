package com.example.DevFlow.controller;

import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public String verInicioCliente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esCliente()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());   //Se pasa el nombre del usuario logueado para mostrarlo en la vista

        return "cliente/inicio";
    }
    
    //-VISTAS GERENTE---------------------------------------------------------------------------------------    
    @GetMapping("/gerente")
    public String verInicioGerente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        return "gerente/inicio";
    }

    
    @GetMapping("/gerente/clientes")
    public String verClientesComoAdmin(
            @RequestParam(required = false) String filtro,
            Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esGerente()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        List<Usuario> usuarios;

        // Si hay filtros, aplica; sino, trae todo
        if ((filtro != null && !filtro.isBlank())) {
            usuarios = usuarioService.obtenerClientesFiltrados(filtro);
        } else {
            usuarios = usuarioService.obtenerClientes();
        }

        // Agrega los datos al modelo
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", filtro);

        return "gerente/listadoDeClientes";
    }
    
    //-VISTAS ADMINISTRADOR---------------------------------------------------------------------------------    
    @GetMapping("/admin")
    public String verInicioAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

        return "administrador/inicio";
    }

    @GetMapping("/admin/usuarios")
    public String verUsuariosComoAdmin(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String rol,
            Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());

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
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        // Muestra la vista con el formulario
        return "administrador/nuevoUsuario";
    }

    @GetMapping("/admin/usuarios/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        try {
            Usuario usuarioEditable = usuarioService.obtenerUsuarioPorId(id);
            model.addAttribute("usuario", usuarioEditable);
            return "administrador/editarUsuario";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/usuarios?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);    //Envio el error desde el servicio
        }
    }

    //-ALTA, BAJA Y MODIFICACION----------------------------------------------------------------------------
    @PostMapping("/admin/usuarios/nuevo")
    public String crearUsuario(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam Long telefono,
            @RequestParam RolUsuario rol,
            @RequestParam String contrasenia,
            HttpSession session,
            Model model) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (!usuarioSesion.esAdministrador()) {
            return "redirect:/login";
        }

        try {
            Usuario nuevo = new Usuario(nombre, contrasenia, email, telefono, rol);
            usuarioService.crearUsuario(nuevo);
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            model.addAttribute("telefono", telefono);
            model.addAttribute("rol", rol);
            model.addAttribute("contrasenia", contrasenia);
            return "administrador/nuevoUsuario";
        }
    }

    @GetMapping("/admin/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
        }

        usuarioService.eliminarUsuario(id);

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
            @ModelAttribute Usuario usuarioActualizado,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!usuario.esAdministrador()) {
            return "redirect:/login";
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

  
}
