package com.example.DevFlow.service;

import com.example.DevFlow.model.Desarrollador;
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

    public List<Desarrollador> obtenerDesarrolladores() {
        return desarrolladorRepository.findAll();
    }

    public Optional<Desarrollador> obtenerDesarrolladorPorId(Long id) {
        return desarrolladorRepository.findById(id);
    }

    public List<Desarrollador> obtenerTodos() {
        return desarrolladorRepository.findAll();
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

            boolean nombreCoincide = dev.getNombre() != null &&
                    dev.getNombre().toLowerCase().contains(filtroLower);
            boolean habilidadesCoinciden = dev.getHabilidades() != null &&
                    dev.getHabilidades().toLowerCase().contains(filtroLower);

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


    public Desarrollador crearDesarrollador(Desarrollador desarrollador) {
        return desarrolladorRepository.save(desarrollador);
    }

    public Desarrollador actualizarDesarrollador(Long id, Desarrollador desarrolladorActualizado) {
        return desarrolladorRepository.findById(id)
                .map(desarrollador -> {
                    desarrollador.setNombre(desarrolladorActualizado.getNombre());
                    desarrollador.setHabilidades(desarrolladorActualizado.getHabilidades());
                    desarrollador.setEstaDisponible(desarrolladorActualizado.getEstaDisponible());
                    return desarrolladorRepository.save(desarrollador);
                })
                .orElse(null);
    }

    public void eliminarDesarrollador(Long id) {
        desarrolladorRepository.deleteById(id);
    }
    
public void actualizarNombreYHabilidades(Long id, String nombre, String habilidades) {
    Optional<Desarrollador> desarrolladorOptional = desarrolladorRepository.findById(id);
    if (desarrolladorOptional.isEmpty()) {
        throw new IllegalArgumentException("El desarrollador no existe.");
    }

    Desarrollador desarrollador = desarrolladorOptional.get();
    desarrollador.setNombre(nombre);
    desarrollador.setHabilidades(habilidades);
    desarrolladorRepository.save(desarrollador);
}


}
