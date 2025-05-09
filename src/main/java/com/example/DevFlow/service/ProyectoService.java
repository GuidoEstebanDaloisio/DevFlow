package com.example.DevFlow.service;

import com.example.DevFlow.model.*;
import static com.example.DevFlow.model.MensajeError.*;
import com.example.DevFlow.repository.DesarrolladorRepository;
import com.example.DevFlow.repository.ProyectoRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private DesarrolladorRepository desarrolladorRepository;

    private MensajeError error;

    public Proyecto crearProyecto(String titulo, String descripcion, String medio_encargo, Double presupuesto, Usuario cliente) {

        // Verificar si el presupuesto es mayor a 0
        if (presupuesto == null || presupuesto <= 0) {
            throw new IllegalArgumentException(PRESUPUESTO_DEBE_SER_MAYOR_A_CERO);
        }

        Proyecto nuevoProyecto = new Proyecto(titulo, descripcion, medio_encargo, presupuesto, cliente);

        return proyectoRepository.save(nuevoProyecto);
    }

    public void eliminarProyecto(Long id) {

        liberarDesarrolladores(obtenerProyectoPorId(id));

        proyectoRepository.deleteById(id);
    }

    public void actualizarProyecto(Long id, Proyecto actualizado) {
        Proyecto existente = obtenerProyectoPorId(id);

        if (existente == null) {
            throw new IllegalArgumentException("Proyecto no encontrado.");
        }

        // Verificar si el presupuesto es mayor a 0
        if (actualizado.getPresupuesto() == null || actualizado.getPresupuesto() <= 0) {
            throw new IllegalArgumentException(PRESUPUESTO_DEBE_SER_MAYOR_A_CERO);
        }
        
        existente.setTitulo(actualizado.getTitulo());
        existente.setDescripcion(actualizado.getDescripcion());
        existente.setMedioEncargo(actualizado.getMedioEncargo());
        existente.setPresupuesto(actualizado.getPresupuesto());

        proyectoRepository.save(existente);
    }

    public List<Proyecto> obtenerProyectos() {
        return proyectoRepository.findAll();
    }

    public int obtenerCantidadProyectosPorEstado(EstadoProyecto estado) {
        List<Proyecto> proyectos = obtenerProyectosPorEstado(estado);
        return proyectos.size();
    }

    public int obtenerCantidadProyectosPorEstadoYCliente(Long idCliente, EstadoProyecto estado) {
        List<Proyecto> proyectos = obtenerProyectosPorEstadoYCliente(idCliente, estado);
        return proyectos.size();
    }

    public List<Proyecto> obtenerProyectosPorEstadoYCliente(Long idCliente, EstadoProyecto estado) {
        List<Proyecto> todosLosProyectos = obtenerProyectosPorIdCliente(idCliente);

        List<Proyecto> proyectosFiltrados = new ArrayList<>();

        for (Proyecto proyecto : todosLosProyectos) {
            boolean coincideConEstado = true;

            if (estado != null) {
                coincideConEstado = proyecto.getEstadoAvance() != null && proyecto.getEstadoAvance().equals(estado);
            }
            if (coincideConEstado) {
                proyectosFiltrados.add(proyecto);
            }
        }
        return proyectosFiltrados;
    }

    public List<Proyecto> obtenerProyectosPorEstado(EstadoProyecto estado) {
        List<Proyecto> todosLosProyectos = obtenerProyectos();

        List<Proyecto> proyectosFiltrados = new ArrayList<>();

        for (Proyecto proyecto : todosLosProyectos) {
            boolean coincideConEstado = true;

            if (estado != null) {
                coincideConEstado = proyecto.getEstadoAvance() != null && proyecto.getEstadoAvance().equals(estado);
            }
            if (coincideConEstado) {
                proyectosFiltrados.add(proyecto);
            }
        }
        return proyectosFiltrados;
    }

    public List<Proyecto> obtenerProyectosPorIdCliente(Long idCliente) {
        return proyectoRepository.findByUsuario_Id(idCliente);
    }

    public List<Proyecto> obtenerProyectosFiltradosParaCliente(Long clienteId, String filtro, String estado) {
        List<Proyecto> todosLosProyectos = obtenerProyectosPorIdCliente(clienteId);

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

    public List<Proyecto> obtenerProyectosFiltrados(String filtro, String estado) {
        List<Proyecto> todosLosProyectos = obtenerProyectos();

        List<Proyecto> proyectosFiltrados = new ArrayList<>();

        for (Proyecto proyecto : todosLosProyectos) {
            boolean coincideConFiltro = true;
            boolean coincideConEstado = true;

            if (filtro != null && !filtro.isBlank()) {
                String filtroMinuscula = filtro.toLowerCase();
                coincideConFiltro = (proyecto.getTitulo() != null && proyecto.getTitulo().toLowerCase().contains(filtroMinuscula))
                        || (proyecto.getUsuario().getNombre() != null && proyecto.getUsuario().getNombre().toLowerCase().contains(filtroMinuscula));
            }

            if (estado != null && !estado.isBlank()) {
                coincideConEstado = proyecto.getEstadoAvance() != null && proyecto.getEstadoAvance().name().equalsIgnoreCase(estado);
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

    public void cambiarEstado(Proyecto proyecto, EstadoProyecto nuevoEstado) {
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto no existe.");
        }

        switch (nuevoEstado) {
            case APROBADO:
                if (proyecto.puedeAprobarse()) {
                    proyecto.setEstadoAvance(EstadoProyecto.APROBADO);
                } else {
                    throw new IllegalArgumentException("El proyecto no puede ser aprobado.");
                }
                break;
            case RECHAZADO:
                if (proyecto.puedeRechazarse()) {
                    proyecto.setEstadoAvance(EstadoProyecto.RECHAZADO);
                } else {
                    throw new IllegalArgumentException("El proyecto no puede ser rechazado.");
                }
                break;
            case CANCELADO:
                if (proyecto.puedeCancelarse()) {
                    liberarDesarrolladores(proyecto);
                    proyecto.setEstadoAvance(EstadoProyecto.CANCELADO);
                } else {
                    throw new IllegalArgumentException("El proyecto no puede ser cancelado.");
                }
                break;
            case EN_PROGRESO:
                if (proyecto.puedeDesarrollarse()) {
                    if (proyecto.getFechaInicio() == null) {
                        throw new IllegalArgumentException("El proyecto no puede comenzar sin fecha de inicio del proyecto");
                    }
                    proyecto.setEstadoAvance(EstadoProyecto.EN_PROGRESO);
                } else {
                    throw new IllegalArgumentException("El proyecto no puede comenzar.");
                }
                break;
            case COMPLETADO:
                if (proyecto.puedeFinalizarse()) {
                    if (proyecto.getFechaFinalizacion() == null) {
                        throw new IllegalArgumentException("El proyecto no puede terminar sin fecha de finalizacion del proyecto");
                    }
                    liberarDesarrolladores(proyecto);
                    proyecto.setEstadoAvance(EstadoProyecto.COMPLETADO);
                } else {
                    throw new IllegalArgumentException("El proyecto no puede finalizarse.");
                }
                break;
            case EN_PAUSA:
                if (proyecto.puedePausarse()) {
                    proyecto.setEstadoAvance(EstadoProyecto.EN_PAUSA);
                } else {
                    throw new IllegalArgumentException("El proyecto no puede pausarse.");
                }
                break;
            default:
                throw new IllegalArgumentException("Estado de proyecto no reconocido.");
        }

        // Finalmente, guardamos el proyecto actualizado en la base de datos
        proyectoRepository.save(proyecto);
    }

    private void liberarDesarrolladores(Proyecto proyecto) {
        List<Desarrollador> desarrolladores = proyecto.getDesarrolladores();
        for (Desarrollador dev : desarrolladores) {
            dev.setEstaDisponible(true);
            dev.desasignarProyecto();
            desarrolladorRepository.save(dev);
        }
        // impiar la lista del proyecto para desvincular desde ambos lados
        proyecto.getDesarrolladores().clear();
    }

    public void establecerFechaInicio(Long idProyecto, Date fechaInicio) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setFechaInicio(fechaInicio);
        proyectoRepository.save(proyecto);
    }

    public void establecerFechaFin(Long idProyecto, Date fechaFin) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setFechaFinalizacion(fechaFin);
        proyectoRepository.save(proyecto);
    }

    public void consultarSiEsPosibleEditarElProyecto(Proyecto proyecto) {
        if (!proyecto.puedeEditarse()) {
            throw new IllegalArgumentException("El proyecto ya no se encuentra en un estado habilitado para la edicion");
        }
    }

    public List<Proyecto> obtenerListadoDeProyectos(String filtro, String estado) {
        // Verifica si hay filtros
        boolean hayFiltros = (filtro != null && !filtro.isBlank()) || (estado != null && !estado.isBlank());

        List<Proyecto> proyectos = hayFiltros
                ? obtenerProyectosFiltrados(filtro, estado)
                : obtenerProyectos();

        return proyectos;
    }

    public List<Proyecto> obtenerListadoDeProyectosParaCliente(Long clienteId, String filtro, String estado) {
        // Verifica si hay filtros
        boolean hayFiltros = (filtro != null && !filtro.isBlank()) || (estado != null && !estado.isBlank());

        List<Proyecto> proyectos = hayFiltros
                ? obtenerProyectosFiltradosParaCliente(clienteId, filtro, estado)
                : obtenerProyectosPorIdCliente(clienteId);

        return proyectos;
    }

}
