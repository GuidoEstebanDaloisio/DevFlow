
package com.example.DevFlow.repository;

import com.example.DevFlow.model.Proyecto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
List<Proyecto> findByUsuario_Id(Long id);

}