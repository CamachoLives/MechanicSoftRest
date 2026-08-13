package com.mechanicsoft.Features.Usuarios.repository;

import com.mechanicsoft.Features.Usuarios.entity.Grupo;
import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    // rolBonus es LAZY y se serializa en el JSON de salida: mismo caso que
    // OrdenServicioRepository/VehiculoRepository — sin esto, listar N grupos
    // dispara N SELECT extra al forzar su carga perezosa.
    @Override
    @EntityGraph(attributePaths = {"rolBonus"})
    List<Grupo> findAll();

    List<Grupo> findByMiembrosContaining(Usuario usuario);

}
