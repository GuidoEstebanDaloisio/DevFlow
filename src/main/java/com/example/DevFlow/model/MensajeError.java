package com.example.DevFlow.model;

public class MensajeError {

    public static final String DESARROLLADOR_NO_EXISTE = "El desarrollador no existe";
    public static final String PRESUPUESTO_DEBE_SER_MAYOR_A_CERO = "El presupuesto debe ser mayor a cero";
    public static final String NO_TIENE_PERMISO_DE_EDITAR_PROYECTO = "No tenés permiso para editar este proyecto";
    public static final String NO_SE_PUEDE_EDITAR_PROYECTO_EN_ESTE_ESTADO = "El proyecto ya no se puede editar";
    public static final String EXISTE_USUARIO_CON_MISMO_NOMBRE_Y_CONTRASENIA = "Ya existe otro usuario con ese nombre y contraseña";
    public static final String EXISTE_USUARIO_CON_MISMO_MAIL = "Ya existe un usuario con ese email";
    

    public static String proyectoNoEncontradoPorId(Long id) {
        return "No se encontró ningún proyecto con la ID: " + id;
    }
    
    public static String desarrolladorNoEncontradoPorId(Long id) {
        return "No se encontró ningún desarrollador con ID: " + id;
    }
    
    public static String usuarioNoEncontradoPorId(Long id) {
        return "No se encontró ningún usuario con la ID: " + id;
    }
}
