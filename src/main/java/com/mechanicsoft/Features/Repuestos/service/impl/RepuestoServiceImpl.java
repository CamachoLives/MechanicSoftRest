package com.mechanicsoft.Features.Repuestos.service.impl;

import com.mechanicsoft.Features.Repuestos.entity.Repuesto;
import com.mechanicsoft.Features.Repuestos.repository.RepuestoRepository;
import com.mechanicsoft.Features.Repuestos.service.interfaces.RepuestoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import com.mechanicsoft.exception.StockInsuficienteException;
import com.mechanicsoft.exception.TransicionInvalidaException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepuestoServiceImpl implements RepuestoService {

    private final RepuestoRepository repository;

    public RepuestoServiceImpl(RepuestoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Repuesto crear(Repuesto repuesto) {
        if (repository.existsByCodigo(repuesto.getCodigo())) {
            throw new TransicionInvalidaException("Ya existe un repuesto con ese código.");
        }
        repuesto.setId(null);
        return repository.save(repuesto);
    }

    @Override
    public List<Repuesto> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return repository.findAll();
        }
        return repository.buscar(buscar.trim());
    }

    @Override
    public Optional<Repuesto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Repuesto actualizar(Long id, Repuesto repuesto) {
        Repuesto existente = obtener(id);

        if (!existente.getCodigo().equals(repuesto.getCodigo()) && repository.existsByCodigo(repuesto.getCodigo())) {
            throw new TransicionInvalidaException("Ya existe un repuesto con ese código.");
        }

        existente.setNombre(repuesto.getNombre());
        existente.setCodigo(repuesto.getCodigo());
        existente.setDescripcion(repuesto.getDescripcion());
        existente.setPrecio(repuesto.getPrecio());
        existente.setCantidadDisponible(repuesto.getCantidadDisponible());

        return repository.save(existente);
    }

    @Override
    public Repuesto cambiarEstado(Long id, boolean activo) {
        Repuesto existente = obtener(id);
        existente.setActivo(activo);
        return repository.save(existente);
    }

    @Override
    public Repuesto registrarEntrada(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new TransicionInvalidaException("La cantidad de entrada debe ser mayor a cero.");
        }
        Repuesto existente = obtener(id);
        existente.setCantidadDisponible(existente.getCantidadDisponible() + cantidad);
        return repository.save(existente);
    }

    @Override
    public Repuesto registrarSalida(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new TransicionInvalidaException("La cantidad de salida debe ser mayor a cero.");
        }
        Repuesto existente = obtener(id);
        if (existente.getCantidadDisponible() < cantidad) {
            throw new StockInsuficienteException("No hay suficiente stock de " + existente.getNombre() + ".");
        }
        existente.setCantidadDisponible(existente.getCantidadDisponible() - cantidad);
        return repository.save(existente);
    }

    private Repuesto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Repuesto no encontrado"));
    }
}
