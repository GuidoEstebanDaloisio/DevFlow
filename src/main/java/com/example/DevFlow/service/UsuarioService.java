package com.example.DevFlow.service;

import com.example.DevFlow.model.MensajeError;
import static com.example.DevFlow.model.MensajeError.*;
import com.example.DevFlow.model.RolUsuario;
import static com.example.DevFlow.model.RolUsuario.CLIENTE;
import com.example.DevFlow.model.Usuario;
import com.example.DevFlow.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    MensajeError error;

    RolUsuario rol;

    public Usuario crearUsuario(String nombre, String contrasenia, String email, Long telefono, RolUsuario rol) {
        
        validarUsuarioParaCarga(email, nombre, contrasenia);
        
        Usuario nuevoUsuario = new Usuario(nombre, contrasenia, email, telefono, rol);

        return usuarioRepository.save(nuevoUsuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = obtenerUsuarioPorId(id);

        String nombreNuevo = usuarioActualizado.getNombre();
        String emailNuevo = usuarioActualizado.getEmail();
        Long telefonoNuevo = usuarioActualizado.getTelefono();
        RolUsuario rolNuevo = usuarioActualizado.getRol();
        String nuevaContrasenia = usuarioActualizado.getContrasenia();

        // Si no se escribe una nueva contraseña, se mantiene la actual
        if (nuevaContrasenia == null || nuevaContrasenia.isBlank()) {
            nuevaContrasenia = usuario.getContrasenia();
        }

        // Actualizar campos
        usuario.setNombre(nombreNuevo);
        usuario.setEmail(emailNuevo);
        usuario.setTelefono(telefonoNuevo);
        usuario.setRol(rolNuevo);
        usuario.setContrasenia(nuevaContrasenia);

        validarUsuarioParaActualizacion(usuario, id);

        usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public int obtenerCantidadUsuarios() {
        List<Usuario> usuarios = obtenerUsuarios();

        return usuarios.size();
    }

    public List<Usuario> obtenerClientes() {
        return obtenerUsuariosPorRol(CLIENTE);
    }

    public List<Usuario> obtenerClientesFiltrados(String filtro) {
        return obtenerUsuariosFiltrados(filtro, rol.CLIENTE.toString());
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException(error.usuarioNoEncontradoPorId(id));
        }

        return usuarioOptional.get();
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario obtenerUsuarioPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    public List<Usuario> obtenerUsuariosPorRol(RolUsuario rol) {
        List<Usuario> todosLosUsuarios = obtenerUsuarios();
        List<Usuario> usuariosFiltrados = new ArrayList<>();

        for (Usuario usuario : todosLosUsuarios) {
            if (usuario.getRol() != null && usuario.getRol() == rol) {
                usuariosFiltrados.add(usuario);
            }
        }

        return usuariosFiltrados;
    }

    public List<Usuario> obtenerUsuariosFiltrados(String filtro, String rol) {
        // Obtener todos los usuarios del repositorio
        List<Usuario> todosLosUsuarios = usuarioRepository.findAll();

        // Crear una lista nueva para guardar los que cumplen con los filtros
        List<Usuario> usuariosFiltrados = new ArrayList<>();

        for (Usuario usuario : todosLosUsuarios) {
            boolean coincideConFiltro = true;
            boolean coincideConRol = true;

            // Verifica si hay que aplicar filtro por nombre o email
            if (filtro != null && !filtro.isBlank()) {
                String filtroMinuscula = filtro.toLowerCase();
                boolean nombreCoincide = usuario.getNombre() != null
                        && usuario.getNombre().toLowerCase().contains(filtroMinuscula);
                boolean emailCoincide = usuario.getEmail() != null
                        && usuario.getEmail().toLowerCase().contains(filtroMinuscula);

                coincideConFiltro = nombreCoincide || emailCoincide;
            }

            // Verifica si hay que aplicar filtro por rol
            if (rol != null && !rol.isBlank()) {
                coincideConRol = usuario.getRol() != null
                        && usuario.getRol().name().equalsIgnoreCase(rol);
            }

            // Si pasa ambos filtros, se agrega a la lista
            if (coincideConFiltro && coincideConRol) {
                usuariosFiltrados.add(usuario);
            }
        }

        return usuariosFiltrados;
    }

    private void validarUsuarioParaCarga(String email, String nombre, String contrasenia) {
        // Verificar si ya existe un usuario con el mismo email
        if (obtenerUsuarioPorEmail(email) != null) {
            throw new IllegalArgumentException(EXISTE_USUARIO_CON_MISMO_MAIL);
        }

        // Verificar si ya existe un usuario con el mismo nombre y contraseña
        for (Usuario usu : obtenerUsuarios()) {
            if (usu.getNombre().equalsIgnoreCase(nombre)
                    && usu.getContrasenia().equals(contrasenia)) {
                throw new IllegalArgumentException(EXISTE_USUARIO_CON_MISMO_NOMBRE_Y_CONTRASENIA);
            }
        }
    }

    private void validarUsuarioParaActualizacion(Usuario usuario, Long idAExcluir) { //Esta version añade un filtro para no tener en cuenta en la busqueda al usuario que estoy editando

        if (obtenerUsuarioPorEmail(usuario.getEmail()) != null
                && !obtenerUsuarioPorEmail(usuario.getEmail()).getId().equals(idAExcluir)) {
            throw new IllegalArgumentException(EXISTE_USUARIO_CON_MISMO_MAIL);
        }

        for (Usuario usu : obtenerUsuarios()) {
            if (!usu.getId().equals(idAExcluir)
                    && usu.getNombre().equalsIgnoreCase(usuario.getNombre())
                    && usu.getContrasenia().equals(usuario.getContrasenia())) {
                throw new IllegalArgumentException(EXISTE_USUARIO_CON_MISMO_NOMBRE_Y_CONTRASENIA);
            }
        }
    }

    public Usuario validarLogin(String nombre, String contrasenia) {
        Usuario usuario = obtenerUsuarioPorNombre(nombre);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (!usuario.getContrasenia().equals(contrasenia)) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return usuario;
    }

    public List<Usuario> obtenerListadoDeClientes(String filtro) {
        // Verifica si hay filtros
        boolean hayFiltros = (filtro != null && !filtro.isBlank());

        List<Usuario> clientes = hayFiltros
                ? obtenerClientesFiltrados(filtro)
                : obtenerClientes();

        return clientes;
    }

    public List<Usuario> obtenerListadoDeUsuarios(String filtro, String rol) {
        boolean hayFiltros = ((filtro != null && !filtro.isBlank()) || (rol != null && !rol.isBlank()));

        List<Usuario> clientes = hayFiltros
                ? obtenerUsuariosFiltrados(filtro, rol)
                : obtenerUsuarios();

        return clientes;
    }

}
