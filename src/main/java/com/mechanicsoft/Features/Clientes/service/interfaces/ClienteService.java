package com.mechanicsoft.Features.Clientes.service.interfaces;

import com.mechanicsoft.Features.Clientes.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    Cliente guardar(Cliente cliente);

    List<Cliente> listar(String buscar);

    Optional<Cliente> buscarPorId(Long id);

    Cliente actualizar(Long id, Cliente cliente);

    Cliente cambiarEstado(Long id, boolean activo);
}
