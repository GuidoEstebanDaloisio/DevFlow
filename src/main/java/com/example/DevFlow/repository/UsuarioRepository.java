package com.example.DevFlow.repository;

import com.example.DevFlow.model.RolUsuario;
import com.example.DevFlow.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNombre(String nombre);
    Usuario findByEmail(String email);
    Optional<Usuario> findByNombreAndContrasenia(String nombre, String contrasenia);
}