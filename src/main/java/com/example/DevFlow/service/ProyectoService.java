package com.example.DevFlow.service;

import com.example.DevFlow.model.*;
import static com.example.DevFlow.model.MensajeError.*;
import com.example.DevFlow.repository.ProyectoRepository;
import java.util.ArrayList;
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
        // Verificar si el presupuesto es mayor a 0
        if (proyecto.getPresupuesto() == null || proyecto.getPresupuesto() <= 0) {
            throw new IllegalArgumentException(PRESUPUESTO_DEBE_SER_MAYOR_A_CERO);
        }
        return proyectoRepository.save(proyecto);
    }

    public void actualizarProyectoComoCliente(Long idProyecto, Usuario cliente, String titulo, String descripcion, String medioEncargo, Double presupuesto) {
        if (presupuesto == null || presupuesto <= 0) {
            throw new IllegalArgumentException(PRESUPUESTO_DEBE_SER_MAYOR_A_CERO);
        }

        Proyecto proyecto = obtenerProyectoPorId(idProyecto);

        //Se vuelve a llamar a "validarPermisoDeEdicionCliente" porque en una aplicación web no se puede confiar en los datos del cliente (puede modificar el id en la URL, o incluso forzar un POST con un proyecto que no le pertenece)
        validarPermisoDeEdicionCliente(proyecto, cliente);

        proyecto.setTitulo(titulo);
        proyecto.setDescripcion(descripcion);
        proyecto.setMedioEncargo(medioEncargo);
        proyecto.setPresupuesto(presupuesto);

        proyectoRepository.save(proyecto);
    }

    public List<Proyecto> obtenerProyectos() {
        return proyectoRepository.findAll();
    }

    public List<Proyecto> obtenerProyectosPorIdCliente(Long idCliente) {
        return proyectoRepository.findByUsuario_Id(idCliente);
    }

    public List<Proyecto> obtenerProyectosFiltradosParaCliente(Long clienteId, String filtro, String estado) {
        List<Proyecto> todosLosProyectos = proyectoRepository.findByUsuario_Id(clienteId);

        List<Proyecto> proyectosFiltrados = new ArrayList<>();

        for (Proyecto proyecto : todosLosProyectos) {
            boolean coincideConFiltro = true;
            boolean coincideConEstado = true;

            if (filtro != null && !filtro.isBlank()) {
                String filtroMinuscula = filtro.toLowerCase();
                coincideConFiltro = proyecto.getTitulo() != null
                        && proyecto.getTitulo().toLowerCase().contains(filtroMinuscula);
            }

            if (estado != null && !estado.isBlank()) {
                coincideConEstado = proyecto.getEstadoAvance() != null
                        && proyecto.getEstadoAvance().name().equalsIgnoreCase(estado);
            }

            if (coincideConFiltro && coincideConEstado) {
                proyectosFiltrados.add(proyecto);
            }
        }

        return proyectosFiltrados;
    }

    public Proyecto obtenerProyectoPorId(Long id) {
        Optional<Proyecto> proyectoOptional = proyectoRepository.findById(id);
        if (proyectoOptional.isEmpty()) {
            throw new IllegalArgumentException(error.proyectoNoEncontradoPorId(id));
        }
        return proyectoOptional.get();
    }

    public Proyecto obtenerProyectoParaEdicionPorCliente(Long idProyecto, Usuario cliente) {
        Proyecto proyecto = obtenerProyectoPorId(idProyecto);

        validarPermisoDeEdicionCliente(proyecto, cliente);

        return proyecto;
    }

    private void validarPermisoDeEdicionCliente(Proyecto proyecto, Usuario cliente) {
        if (!proyecto.getUsuario().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException(NO_TIENE_PERMISO_DE_EDITAR_PROYECTO);
        }

        if (proyecto.getEstadoAvance() != EstadoProyecto.ESPERANDO_REVISION) {
            throw new IllegalArgumentException(NO_SE_PUEDE_EDITAR_PROYECTO_EN_ESTE_ESTADO);
        }
    }

}
