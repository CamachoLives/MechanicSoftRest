package com.mechanicsoft.Features.Repuestos.service.interfaces;

import com.mechanicsoft.Features.Repuestos.entity.Repuesto;

import java.util.List;
import java.util.Optional;

public interface RepuestoService {

    Repuesto crear(Repuesto repuesto);

    List<Repuesto> listar(String buscar);

    Optional<Repuesto> buscarPorId(Long id);

    Repuesto actualizar(Long id, Repuesto repuesto);

    Repuesto cambiarEstado(Long id, boolean activo);

    Repuesto registrarEntrada(Long id, int cantidad);

    Repuesto registrarSalida(Long id, int cantidad);

}
