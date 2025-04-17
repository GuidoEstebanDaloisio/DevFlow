package com.example.DevFlow.service;

import static com.example.DevFlow.model.MensajeError.*;
import com.example.DevFlow.model.RolUsuario;
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

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario obtenerUsuarioPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
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

        // Validar si ya existe otro usuario con la misma combinación nombre + contraseña
        Optional<Usuario> existente = usuarioRepository.findByNombreAndContrasenia(nombreNuevo, nuevaContrasenia);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new IllegalArgumentException(EXISTE_USUARIO_CON_MISMO_NOMBRE_O_CONTRASENIA);
        }

        // Actualizar campos
        usuario.setNombre(nombreNuevo);
        usuario.setEmail(emailNuevo);
        usuario.setTelefono(telefonoNuevo);
        usuario.setRol(rolNuevo);
        usuario.setContrasenia(nuevaContrasenia);

        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
    }

}
