package com.example.DevFlow.controller;

import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario, HttpSession session) {
        Usuario encontrado = usuarioService.obtenerUsuarioPorNombre(usuario.getNombre());

        if (encontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        if (!encontrado.getContrasenia().equals(usuario.getContrasenia())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        session.setAttribute("usuario", encontrado); // Guardamos el usuario en sesión

        // Enviamos el rol al frontend para redirigir
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("rol", encontrado.getRol().toString());

        return ResponseEntity.ok(respuesta);
    }

}
