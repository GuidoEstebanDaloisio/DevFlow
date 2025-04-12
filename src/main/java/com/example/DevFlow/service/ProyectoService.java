package com.example.DevFlow.service;

import com.example.DevFlow.model.*;
import com.example.DevFlow.repository.ProyectoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;
    
    private MensajeError error;

    public Proyecto crearProyecto(Proyecto proyecto) {
        if (proyecto.getPresupuesto() == null || proyecto.getPresupuesto() <= 0) {
            throw new IllegalArgumentException(error.PRESUPUESTO_DEBE_SER_MAYOR_A_CERO);
        }
        return proyectoRepository.save(proyecto);
    }

    public Proyecto validarEdicionProyectoPorCliente(Long idProyecto, Usuario cliente) {
        Proyecto proyecto = obtenerProyectoPorId(idProyecto);

        if (!proyecto.getUsuario().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException(error.NO_TIENE_PERMISO_DE_EDITAR_PROYECTO);
        }

        if (proyecto.getEstadoAvance() != EstadoProyecto.ESPERANDO_REVISION) {
            throw new IllegalArgumentException(error.NO_SE_PUEDE_EDITAR_PROYECTO_EN_ESTE_ESTADO);
        }

        return proyecto;
    }

    public void actualizarProyectoCliente(Long idProyecto, Usuario cliente, String titulo, String descripcion,
            String medioEncargo, Double presupuesto) {

        if (presupuesto == null || presupuesto <= 0) {
            throw new IllegalArgumentException(error.PRESUPUESTO_DEBE_SER_MAYOR_A_CERO);
        }

        Proyecto proyecto = validarEdicionProyectoPorCliente(idProyecto, cliente);

        proyecto.setTitulo(titulo);
        proyecto.setDescripcion(descripcion);
        proyecto.setMedioEncargo(medioEncargo);
        proyecto.setPresupuesto(presupuesto);

        proyectoRepository.save(proyecto);
    }

    public List<Proyecto> obtenerProyectos() {
        return proyectoRepository.findAll();
    }

    public Proyecto obtenerProyectoPorId(Long id) {
        Optional<Proyecto> proyectoOptional = proyectoRepository.findById(id);
        if (proyectoOptional.isEmpty()) {
            throw new IllegalArgumentException(error.proyectoNoEncontradoPorId(id));
        }
        return proyectoOptional.get();
    }

    public List<Proyecto> obtenerProyectosPorIdCliente(Long idCliente) {
        return proyectoRepository.findByUsuario_Id(idCliente);
    }
}
