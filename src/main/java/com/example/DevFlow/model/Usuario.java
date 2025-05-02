package com.example.DevFlow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.List;

@Entity(name = "Usuario")
public class Usuario {

    @Id
    @SequenceGenerator(
            name = "secuencia_usuario",
            sequenceName = "secuencia_usuario",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "secuencia_usuario"
    )
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, columnDefinition = "TEXT")
    private String nombre;

    @Column(name = "contrasenia", nullable = false, columnDefinition = "TEXT")
    private String contrasenia;

    @Column(name = "email", nullable = false, columnDefinition = "TEXT", unique = true)
    private String email;

    @Column(name = "telefono", nullable = false)
    private Long telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private RolUsuario rol;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<Proyecto> proyectos;

    public Usuario() {
    }

    public Usuario(String nombre, String contrasenia, String email, Long telefono, RolUsuario rol) {
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
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

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public boolean esCliente() {
        return rol.equals(RolUsuario.CLIENTE);
    }

    public boolean esGerente() {
        return rol.equals(RolUsuario.GERENTE);
    }
    
    public boolean esAdministrador() {
        return rol.equals(RolUsuario.ADMINISTRADOR);
    }
}
