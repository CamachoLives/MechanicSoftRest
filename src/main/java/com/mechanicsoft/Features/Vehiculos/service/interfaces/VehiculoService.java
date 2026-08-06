package com.mechanicsoft.Features.Vehiculos.service.interfaces;

import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;

import java.util.List;
import java.util.Optional;

public interface VehiculoService {

    Vehiculo guardar(Vehiculo vehiculo);

    List<Vehiculo> listar();

    List<Vehiculo> listarPorCliente(Long clienteId);

    Optional<Vehiculo> buscarPorId(Long id);

    Optional<Vehiculo> buscarPorPlaca(String placa);

    Vehiculo actualizar(Long id, Vehiculo vehiculo);

    Vehiculo cambiarEstado(Long id, boolean activo);

}
