package com.example.DevFlow.controller;

import static com.example.DevFlow.model.RolUsuario.ADMINISTRADOR;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public String login(
            @RequestParam String nombre,
            @RequestParam String contrasenia,
            HttpSession session,
            Model model) {

        try {
            Usuario usuario = usuarioService.validarLogin(nombre, contrasenia);
            session.setAttribute("usuario", usuario);
            model.addAttribute("usuario", usuario);

            switch (usuario.getRol()) {
                case ADMINISTRADOR:
                    return "redirect:/admin";

                case GERENTE:
                    return "redirect:/gerente";

                case CLIENTE:
                    return "redirect:/cliente";
                default:
                    return "redirect:/login?error=" + URLEncoder.encode("Este usuario tiene un rol no implementado", StandardCharsets.UTF_8);
            }

        } catch (IllegalArgumentException e) {
            return "redirect:/login?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
