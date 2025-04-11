package com.example.DevFlow.model;

public enum EstadoProyecto {
    ESPERANDO_REVISION("Esperando revisión"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    EN_PROGRESO("En progreso"),
    COMPLETADO("Completado"),
    CANCELADO("Cancelado"),
    EN_PAUSA("En pausa");

    private final String etiqueta;

    EstadoProyecto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}