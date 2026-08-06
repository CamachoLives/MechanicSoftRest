package com.mechanicsoft.Features.Usuarios.service.interfaces;

import com.mechanicsoft.Features.Usuarios.entity.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {

    Rol crear(Rol rol);

    List<Rol> listar();

    Optional<Rol> buscarPorId(Long id);

    Rol actualizar(Long id, Rol rol);

    void eliminar(Long id);

}
