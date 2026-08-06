package com.mechanicsoft.Features.Usuarios.repository;

import com.mechanicsoft.Features.Usuarios.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {

    boolean existsByNombre(String nombre);

}
