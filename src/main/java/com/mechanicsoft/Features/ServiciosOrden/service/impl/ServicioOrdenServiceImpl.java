package com.mechanicsoft.Features.ServiciosOrden.service.impl;

import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.service.interfaces.OrdenServicioService;
import com.mechanicsoft.Features.ServiciosOrden.entity.ServicioOrden;
import com.mechanicsoft.Features.ServiciosOrden.repository.ServicioOrdenRepository;
import com.mechanicsoft.Features.ServiciosOrden.service.interfaces.ServicioOrdenService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicioOrdenServiceImpl implements ServicioOrdenService {

    private final ServicioOrdenRepository repository;
    private final OrdenServicioService ordenService;

    public ServicioOrdenServiceImpl(ServicioOrdenRepository repository, OrdenServicioService ordenService) {
        this.repository = repository;
        this.ordenService = ordenService;
    }

    @Override
    @Transactional
    public ServicioOrden agregar(Long ordenId, ServicioOrden servicio) {
        OrdenServicio orden = ordenService.obtenerOrden(ordenId);
        ordenService.verificarMutable(orden);

        servicio.setId(null);
        servicio.setOrden(orden);
        servicio.setSubtotal(servicio.getPrecioUnitario().multiply(BigDecimal.valueOf(servicio.getCantidad())));

        ServicioOrden guardado = repository.save(servicio);
        ordenService.recalcularValorTotal(ordenId);
        return guardado;
    }

    @Override
    public List<ServicioOrden> listarPorOrden(Long ordenId) {
        return repository.findByOrdenId(ordenId);
    }

    @Override
    @Transactional
    public void eliminar(Long ordenId, Long lineaId) {
        OrdenServicio orden = ordenService.obtenerOrden(ordenId);
        ordenService.verificarMutable(orden);

        ServicioOrden linea = repository.findById(lineaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Línea de servicio no encontrada"));
        if (!linea.getOrden().getId().equals(ordenId)) {
            throw new RecursoNoEncontradoException("Línea de servicio no encontrada");
        }

        repository.delete(linea);
        ordenService.recalcularValorTotal(ordenId);
    }
}
