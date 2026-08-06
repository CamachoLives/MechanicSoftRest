package com.mechanicsoft.Features.Clientes.service.impl;

import com.mechanicsoft.Features.Clientes.entity.Cliente;
import com.mechanicsoft.Features.Clientes.repository.ClienteRepository;
import com.mechanicsoft.Features.Clientes.service.interfaces.ClienteService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return repository.save(cliente);
    }

    @Override
    public List<Cliente> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return repository.findAll();
        }
        return repository.buscar(buscar.trim());
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Cliente actualizar(Long id, Cliente cliente) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        existente.setNombre(cliente.getNombre());
        existente.setTelefono(cliente.getTelefono());
        existente.setCorreo(cliente.getCorreo());

        return repository.save(existente);
    }

    @Override
    public Cliente cambiarEstado(Long id, boolean activo) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        existente.setActivo(activo);
        return repository.save(existente);
    }
}
