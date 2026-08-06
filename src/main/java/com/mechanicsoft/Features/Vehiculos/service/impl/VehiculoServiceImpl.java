package com.mechanicsoft.Features.Vehiculos.service.impl;

import com.mechanicsoft.Features.Clientes.entity.Cliente;
import com.mechanicsoft.Features.Clientes.repository.ClienteRepository;
import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import com.mechanicsoft.Features.Vehiculos.repository.VehiculoRepository;
import com.mechanicsoft.Features.Vehiculos.service.interfaces.VehiculoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository repository;
    private final ClienteRepository clienteRepository;

    public VehiculoServiceImpl(VehiculoRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        vehiculo.setCliente(resolverCliente(vehiculo));
        return repository.save(vehiculo);
    }

    @Override
    public List<Vehiculo> listar() {
        return repository.findAll();
    }

    @Override
    public List<Vehiculo> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    @Override
    public Optional<Vehiculo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        return repository.findByPlaca(placa);
    }

    @Override
    public Vehiculo actualizar(Long id, Vehiculo vehiculo) {

        Vehiculo existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));

        existente.setPlaca(vehiculo.getPlaca());
        existente.setMarca(vehiculo.getMarca());
        existente.setModelo(vehiculo.getModelo());
        existente.setAnio(vehiculo.getAnio());
        existente.setColor(vehiculo.getColor());
        existente.setKilometraje(vehiculo.getKilometraje());
        existente.setCilindrajeCc(vehiculo.getCilindrajeCc());
        existente.setFotoVehiculo(vehiculo.getFotoVehiculo());
        existente.setObservaciones(vehiculo.getObservaciones());
        existente.setCliente(resolverCliente(vehiculo));

        return repository.save(existente);
    }

    @Override
    public Vehiculo cambiarEstado(Long id, boolean activo) {
        Vehiculo existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));

        existente.setActivo(activo);
        return repository.save(existente);
    }

    private Cliente resolverCliente(Vehiculo vehiculo) {
        if (vehiculo.getCliente() == null || vehiculo.getCliente().getId() == null) {
            throw new RecursoNoEncontradoException("El vehículo debe tener un cliente asociado");
        }
        return clienteRepository.findById(vehiculo.getCliente().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
    }
}
