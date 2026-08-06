package com.mechanicsoft.Features.RepuestosOrden.service.impl;

import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.service.interfaces.OrdenServicioService;
import com.mechanicsoft.Features.RepuestosOrden.entity.RepuestoOrden;
import com.mechanicsoft.Features.RepuestosOrden.repository.RepuestoOrdenRepository;
import com.mechanicsoft.Features.RepuestosOrden.service.interfaces.RepuestoOrdenService;
import com.mechanicsoft.Features.Repuestos.entity.Repuesto;
import com.mechanicsoft.Features.Repuestos.repository.RepuestoRepository;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import com.mechanicsoft.exception.StockInsuficienteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RepuestoOrdenServiceImpl implements RepuestoOrdenService {

    private final RepuestoOrdenRepository repository;
    private final RepuestoRepository repuestoRepository;
    private final OrdenServicioService ordenService;

    public RepuestoOrdenServiceImpl(RepuestoOrdenRepository repository, RepuestoRepository repuestoRepository,
                                     OrdenServicioService ordenService) {
        this.repository = repository;
        this.repuestoRepository = repuestoRepository;
        this.ordenService = ordenService;
    }

    @Override
    @Transactional
    public RepuestoOrden agregar(Long ordenId, RepuestoOrden linea) {
        OrdenServicio orden = ordenService.obtenerOrden(ordenId);
        ordenService.verificarMutable(orden);

        if (linea.getRepuesto() == null || linea.getRepuesto().getId() == null) {
            throw new RecursoNoEncontradoException("El repuesto es obligatorio");
        }
        Repuesto repuesto = repuestoRepository.findById(linea.getRepuesto().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Repuesto no encontrado"));

        if (repuesto.getCantidadDisponible() < linea.getCantidadUtilizada()) {
            throw new StockInsuficienteException("No hay suficiente stock de " + repuesto.getNombre() + ".");
        }

        repuesto.setCantidadDisponible(repuesto.getCantidadDisponible() - linea.getCantidadUtilizada());
        repuestoRepository.save(repuesto);

        linea.setId(null);
        linea.setOrden(orden);
        linea.setRepuesto(repuesto);
        linea.setPrecioUnitario(repuesto.getPrecio());
        linea.setSubtotal(repuesto.getPrecio().multiply(BigDecimal.valueOf(linea.getCantidadUtilizada())));

        RepuestoOrden guardado = repository.save(linea);
        ordenService.recalcularValorTotal(ordenId);
        return guardado;
    }

    @Override
    public List<RepuestoOrden> listarPorOrden(Long ordenId) {
        return repository.findByOrdenId(ordenId);
    }

    @Override
    @Transactional
    public void eliminar(Long ordenId, Long lineaId) {
        OrdenServicio orden = ordenService.obtenerOrden(ordenId);
        ordenService.verificarMutable(orden);

        RepuestoOrden linea = repository.findById(lineaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Línea de repuesto no encontrada"));
        if (!linea.getOrden().getId().equals(ordenId)) {
            throw new RecursoNoEncontradoException("Línea de repuesto no encontrada");
        }

        Repuesto repuesto = linea.getRepuesto();
        repuesto.setCantidadDisponible(repuesto.getCantidadDisponible() + linea.getCantidadUtilizada());
        repuestoRepository.save(repuesto);

        repository.delete(linea);
        ordenService.recalcularValorTotal(ordenId);
    }
}
