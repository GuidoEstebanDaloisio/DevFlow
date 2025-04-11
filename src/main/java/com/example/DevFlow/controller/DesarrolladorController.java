package com.example.DevFlow.controller;


import com.example.DevFlow.model.Desarrollador;
import com.example.DevFlow.service.DesarrolladorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/desarrolladores")
public class DesarrolladorController {

    @Autowired
    private DesarrolladorService desarrolladorService;

    @GetMapping
    public List<Desarrollador> obtenerDesarrolladores() {
        return desarrolladorService.obtenerDesarrolladores();
    }

    @PostMapping
    public Desarrollador crearDesarrollador(@RequestBody Desarrollador desarrollador) {
        return desarrolladorService.crearDesarrollador(desarrollador);
    }
}