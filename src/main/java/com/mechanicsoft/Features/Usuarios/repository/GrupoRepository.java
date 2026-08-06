package com.mechanicsoft.Features.Usuarios.repository;

import com.mechanicsoft.Features.Usuarios.entity.Grupo;
import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByMiembrosContaining(Usuario usuario);

}
