package com.example.DevFlow.model;

import static com.example.DevFlow.model.EstadoProyecto.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

@Entity(name = "Proyecto")
public class Proyecto {

    @Id
    @SequenceGenerator(
            name = "secuencia_proyecto",
            sequenceName = "secuencia_proyecto",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "secuencia_proyecto"
    )
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "titulo", nullable = false, columnDefinition = "TEXT")
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "medio_encargo", nullable = false, columnDefinition = "TEXT")
    private String medioEncargo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_inicio")
    private Date fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_finalizacion")
    private Date fechaFinalizacion;

    @Column(name = "presupuesto", nullable = false)
    private Double presupuesto;

    @Enumerated(EnumType.STRING) // Esto lo hace funcionar correctamente con la base de datos
    @Column(name = "estado_avance", nullable = false)
    private EstadoProyecto estadoAvance;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnore  // Evita bucle infinito al serializar
    private Usuario usuario;

    @OneToMany(mappedBy = "proyecto")
    @JsonIgnore  // Evita el bucle infinito al serializar
    private List<Desarrollador> desarrolladores;

    public Proyecto() {
    }

    public Proyecto(String titulo, String descripcion, String medioEncargo, Double presupuesto, Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.medioEncargo = medioEncargo;
        this.presupuesto = presupuesto;
        this.usuario = usuario;
        this.estadoAvance = EstadoProyecto.ESPERANDO_REVISION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMedioEncargo() {
        return medioEncargo;
    }

    public void setMedioEncargo(String medioEncargo) {
        this.medioEncargo = medioEncargo;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public EstadoProyecto getEstadoAvance() {
        return estadoAvance;
    }

    public void setEstadoAvance(EstadoProyecto estadoAvance) {
        this.estadoAvance = estadoAvance;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Desarrollador> getDesarrolladores() {
        return desarrolladores;
    }

    public void setDesarrolladores(List<Desarrollador> desarrolladores) {
        this.desarrolladores = desarrolladores;
    }

    public boolean puedeVolverARevision() {
        return false;
    }

    public boolean puedeAprobarse() {
        return estadoAvance == EstadoProyecto.ESPERANDO_REVISION;
    }

    public boolean puedeRechazarse() {
        return estadoAvance == EstadoProyecto.ESPERANDO_REVISION;
    }

    public boolean puedeCancelarse() {
        return estadoAvance == EstadoProyecto.EN_PROGRESO || estadoAvance == EstadoProyecto.EN_PAUSA;
    }

    public boolean puedeDesarrollarse() {
        return estadoAvance == EstadoProyecto.APROBADO || estadoAvance == EstadoProyecto.EN_PAUSA;
    }

    public boolean puedeFinalizarse() {
        return estadoAvance == EstadoProyecto.EN_PROGRESO;
    }

    public boolean puedePausarse() {
        return estadoAvance == EstadoProyecto.EN_PROGRESO;
    }

    public void liberarDesarrollador(Desarrollador desarrollador) {
        if (desarrolladores != null) {
            desarrolladores.remove(desarrollador);
        }
    }
    
    public boolean puedeEditarse(){ //Los datos del proyecto solo pueden editarse mientras esten en revision ya que sino seria un peligro para el cliente
        return estadoAvance == EstadoProyecto.ESPERANDO_REVISION;
    }

}
