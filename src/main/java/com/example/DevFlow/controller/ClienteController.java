package com.example.DevFlow.controller;

import com.example.DevFlow.model.Proyecto;
import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.ProyectoService;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClienteController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/cliente")
    public String vistaCliente(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login"; // Si no hay sesión, redirige
        }

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "cliente";
    }

    /*@GetMapping("/cliente/proyectos")
    public String vistaProyectos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        verificarQueSeaCliente(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "proyectos-cliente";
    }*/



    private String verificarQueSeaCliente(Usuario usuario) {
        if (usuario == null || usuario.getRol() != RolUsuario.CLIENTE) {
            return "redirect:/login";
        }
        return null;
    }
}
