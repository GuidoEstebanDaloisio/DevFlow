package com.example.DevFlow.service;

import com.example.DevFlow.model.Desarrollador;
import com.example.DevFlow.model.MensajeError;
import static com.example.DevFlow.model.MensajeError.DESARROLLADOR_NO_EXISTE;
import com.example.DevFlow.model.Proyecto;
import com.example.DevFlow.repository.DesarrolladorRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DesarrolladorService {

    @Autowired
    private DesarrolladorRepository desarrolladorRepository;

    @Autowired
    private ProyectoService proyectoService;

    private MensajeError error;

    public Desarrollador crearDesarrollador(String nombre, String habilidades) {
        Desarrollador nuevoDesarrollador = new Desarrollador(nombre, habilidades);

        return desarrolladorRepository.save(nuevoDesarrollador);
    }

    public void eliminarDesarrollador(Long id) {
        desarrolladorRepository.deleteById(id);
    }

    public void asignarAProyecto(Long idProyecto, Long idDesarrollador) {
        Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
        Desarrollador desarrollador = obtenerDesarrolladorPorId(idDesarrollador);

        if (proyecto == null) {
            throw new IllegalArgumentException("No se encontró el proyecto");
        } else if (desarrollador == null) {
            throw new IllegalArgumentException("No se encontró el desarrollador");
        } else {
            desarrollador.setProyecto(proyecto);
            proyecto.getDesarrolladores().add(desarrollador);

            desarrolladorRepository.save(desarrollador);
        }
    }

    public void desasignarAProyecto(Long idProyecto, Long idDesarrollador) {
        Proyecto proyecto = proyectoService.obtenerProyectoPorId(idProyecto);
        Desarrollador desarrollador = obtenerDesarrolladorPorId(idDesarrollador);

        if (proyecto == null) {
            throw new IllegalArgumentException("No se encontró el proyecto");
        } else if (desarrollador == null) {
            throw new IllegalArgumentException("No se encontró el desarrollador");
        } else {
            desarrollador.desasignarProyecto();

            proyecto.liberarDesarrollador(desarrollador);
            desarrolladorRepository.save(desarrollador);
        }

    }

    public void actualizarNombreYHabilidades(Long id, String nombre, String habilidades) {
        Optional<Desarrollador> desarrolladorOptional = desarrolladorRepository.findById(id);
        if (desarrolladorOptional.isEmpty()) {
            throw new IllegalArgumentException(DESARROLLADOR_NO_EXISTE);
        }

        Desarrollador desarrollador = desarrolladorOptional.get();
        desarrollador.setNombre(nombre);
        desarrollador.setHabilidades(habilidades);
        desarrolladorRepository.save(desarrollador);
    }

    public List<Desarrollador> obtenerDesarrolladores() {
        return desarrolladorRepository.findAll();
    }

    public int obtenerCantidadDesarrolladoresDisponibles() {
        List<Desarrollador> desarrolladores = obtenerDesarrolladoresDisponibles();
        return desarrolladores.size();
    }

    public Desarrollador obtenerDesarrolladorPorId(Long id) {
        Optional<Desarrollador> desarrolladorOptional = desarrolladorRepository.findById(id);

        if (desarrolladorOptional.isEmpty()) {
            throw new IllegalArgumentException(error.desarrolladorNoEncontradoPorId(id));
        }

        return desarrolladorOptional.get();
    }

    public List<Desarrollador> obtenerPorProyecto(Proyecto proyecto) {
        List<Desarrollador> todos = obtenerDesarrolladores();
        List<Desarrollador> asignados = new ArrayList<>();

        for (Desarrollador dev : todos) {
            if (dev.getProyecto() != null && dev.getProyecto().getId().equals(proyecto.getId())) {
                asignados.add(dev);
            }
        }

        return asignados;
    }

    public List<Desarrollador> obtenerDesarrolladoresDisponibles() {
        return obtenerDesarrolladoresFiltrados(null, "DISPONIBLE");
    }

    public List<Desarrollador> obtenerDesarrolladoresFiltrados(String filtro, String estado) {
        // Obtener todos los desarrolladores del repositorio
        List<Desarrollador> todosLosDesarrolladores = desarrolladorRepository.findAll();

        // Lista filtrada a retornar
        List<Desarrollador> desarrolladoresFiltrados = new ArrayList<>();

        for (Desarrollador dev : todosLosDesarrolladores) {
            boolean coincideConFiltro = true;
            boolean coincideConEstado = true;

            // Filtrado por nombre o habilidades
            if (filtro != null && !filtro.isBlank()) {
                String filtroLower = filtro.toLowerCase();

                boolean nombreCoincide = dev.getNombre() != null
                        && dev.getNombre().toLowerCase().contains(filtroLower);
                boolean habilidadesCoinciden = dev.getHabilidades() != null
                        && dev.getHabilidades().toLowerCase().contains(filtroLower);

                coincideConFiltro = nombreCoincide || habilidadesCoinciden;
            }

            // Filtrado por estado: DISPONIBLE o ASIGNADO
            if (estado != null && !estado.isBlank()) {
                if (estado.equalsIgnoreCase("DISPONIBLE")) {
                    coincideConEstado = dev.getEstaDisponible() != null && dev.getEstaDisponible();
                } else if (estado.equalsIgnoreCase("ASIGNADO")) {
                    coincideConEstado = dev.getEstaDisponible() != null && !dev.getEstaDisponible();
                }
            }

            // Si cumple con ambos filtros, lo agregamos a la lista
            if (coincideConFiltro && coincideConEstado) {
                desarrolladoresFiltrados.add(dev);
            }
        }
        return desarrolladoresFiltrados;
    }

    public List<Desarrollador> obtenerListadoDeDesarrolladores(String filtro, String estado) {
        // Verifica si hay filtros
        boolean hayFiltros = (filtro != null && !filtro.isBlank()) || (estado != null && !estado.isBlank());

        List<Desarrollador> desarrolladores = hayFiltros
                ? obtenerDesarrolladoresFiltrados(filtro, estado)
                : obtenerDesarrolladores();

        return desarrolladores;
    }

}
