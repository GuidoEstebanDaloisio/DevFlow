package com.example.DevFlow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity(name = "Desarrollador")
public class Desarrollador {

    @Id
    @SequenceGenerator(
            name = "secuencia_desarrollador",
            sequenceName = "secuencia_desarrollador",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "secuencia_desarrollador"
    )
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, columnDefinition = "TEXT")
    private String nombre;

    @Column(name = "habilidades", columnDefinition = "TEXT")
    private String habilidades;

    @Column(name = "esta_disponible", nullable = false)
    private Boolean estaDisponible;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    @JsonIgnore  // Evita bucle infinito al serializar
    private Proyecto proyecto;

    public Desarrollador() {
    }

    public Desarrollador(String nombre, String habilidades) {
        this.nombre = nombre;
        this.habilidades = habilidades;
        this.estaDisponible = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(String habilidades) {
        this.habilidades = habilidades;
    }

    public Boolean getEstaDisponible() {
        return estaDisponible;
    }

    public void setEstaDisponible(Boolean estaDisponible) {
        this.estaDisponible = estaDisponible;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
        estaDisponible = false;
    }

    public void desasignarProyecto() {
        setProyecto(null);
        estaDisponible = true;
    }

}
