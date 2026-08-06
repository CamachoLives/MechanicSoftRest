package com.mechanicsoft.Features.Usuarios.service.interfaces;

import com.mechanicsoft.Features.Usuarios.entity.Grupo;

import java.util.List;
import java.util.Optional;

public interface GrupoService {

    Grupo crear(Grupo grupo);

    List<Grupo> listar();

    Optional<Grupo> buscarPorId(Long id);

    Grupo actualizar(Long id, Grupo grupo);

    void eliminar(Long id);

}
