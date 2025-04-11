package com.example.DevFlow.service;

import com.example.DevFlow.model.*;
import com.example.DevFlow.repository.DesarrolladorRepository;
import com.example.DevFlow.repository.ProyectoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private DesarrolladorRepository desarrolladorRepository;

// Crear un nuevo proyecto
    public Proyecto crearProyecto(Proyecto proyecto) {
        if (proyecto.getPresupuesto() == null || proyecto.getPresupuesto() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor a cero.");
        }
        return proyectoRepository.save(proyecto);
    }

    public Proyecto validarEdicionProyectoPorCliente(Long idProyecto, Usuario cliente) {
        Proyecto proyecto = obtenerProyectoPorId(idProyecto);

        if (!proyecto.getUsuario().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("No tenés permiso para editar este proyecto.");
        }

        if (proyecto.getEstadoAvance() != EstadoProyecto.ESPERANDO_REVISION) {
            throw new IllegalArgumentException("Este proyecto no se puede editar en su estado actual.");
        }

        return proyecto;
    }

    public void actualizarProyectoCliente(Long idProyecto, Usuario cliente, String titulo, String descripcion,
            String medioEncargo, Double presupuesto) {

        if (presupuesto == null || presupuesto <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor a cero.");
        }

        Proyecto proyecto = validarEdicionProyectoPorCliente(idProyecto, cliente);

        proyecto.setTitulo(titulo);
        proyecto.setDescripcion(descripcion);
        proyecto.setMedioEncargo(medioEncargo);
        proyecto.setPresupuesto(presupuesto);

        proyectoRepository.save(proyecto);
    }

    // Actualizar un proyecto existente
    public Proyecto actualizarProyecto(Long id, Proyecto proyecto) {
        Optional<Proyecto> proyectoExistente = proyectoRepository.findById(id);
        if (proyectoExistente.isEmpty()) {
            throw new IllegalArgumentException("El proyecto con ID: " + id + " no existe.");
        }
        proyecto.setId(id);
        return proyectoRepository.save(proyecto);
    }

    // Obtener todos los proyectos
    public List<Proyecto> obtenerProyectos() {
        return proyectoRepository.findAll();
    }

    // Obtener un proyecto por ID
    public Proyecto obtenerProyectoPorId(Long id) {
        Optional<Proyecto> proyectoOptional = proyectoRepository.findById(id);
        if (proyectoOptional.isEmpty()) {
            throw new IllegalArgumentException("Proyecto no encontrado con ID: " + id);
        }
        return proyectoOptional.get();
    }

    // Obtener un proyecto por ID del cliente
    public List<Proyecto> obtenerProyectosPorIdCliente(Long idCliente) {
        return proyectoRepository.findByUsuario_Id(idCliente);
    }

    // Eliminar un proyecto por ID
    public void eliminarProyecto(Long id) {
        Optional<Proyecto> proyectoOptional = proyectoRepository.findById(id);
        if (proyectoOptional.isPresent()) {
            Proyecto proyecto = proyectoOptional.get();
            // Actualizar la disponibilidad de los desarrolladores
            proyecto.getDesarrolladores().forEach(desarrollador -> {
                desarrollador.setEstaDisponible(true);
                desarrolladorRepository.save(desarrollador);
            });
            proyectoRepository.deleteById(id);
        }
    }
}
