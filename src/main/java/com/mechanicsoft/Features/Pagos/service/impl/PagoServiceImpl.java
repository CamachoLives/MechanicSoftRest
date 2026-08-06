package com.mechanicsoft.Features.Pagos.service.impl;

import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.service.interfaces.OrdenServicioService;
import com.mechanicsoft.Features.Pagos.entity.EstadoPago;
import com.mechanicsoft.Features.Pagos.entity.Pago;
import com.mechanicsoft.Features.Pagos.repository.PagoRepository;
import com.mechanicsoft.Features.Pagos.service.interfaces.PagoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository repository;
    private final OrdenServicioService ordenService;

    public PagoServiceImpl(PagoRepository repository, OrdenServicioService ordenService) {
        this.repository = repository;
        this.ordenService = ordenService;
    }

    @Override
    public Pago registrar(Long ordenId, Pago pago) {
        OrdenServicio orden = ordenService.obtenerOrden(ordenId);
        ordenService.verificarMutable(orden);

        pago.setId(null);
        pago.setOrden(orden);
        pago.setEstado(EstadoPago.PAGADO);

        return repository.save(pago);
    }

    @Override
    public List<Pago> listarPorOrden(Long ordenId) {
        return repository.findByOrdenId(ordenId);
    }

    @Override
    public Pago anular(Long ordenId, Long pagoId) {
        Pago pago = repository.findById(pagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));
        if (!pago.getOrden().getId().equals(ordenId)) {
            throw new RecursoNoEncontradoException("Pago no encontrado");
        }

        pago.setEstado(EstadoPago.ANULADO);
        return repository.save(pago);
    }

    @Override
    public Map<String, Object> resumenPago(Long ordenId) {
        OrdenServicio orden = ordenService.obtenerOrden(ordenId);

        BigDecimal totalPagado = repository.findByOrdenId(ordenId).stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String estado;
        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            estado = "PENDIENTE";
        } else if (totalPagado.compareTo(orden.getValorTotal()) >= 0) {
            estado = "PAGADO";
        } else {
            estado = "PAGO_PARCIAL";
        }

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("valorTotal", orden.getValorTotal());
        resumen.put("totalPagado", totalPagado);
        resumen.put("saldoPendiente", orden.getValorTotal().subtract(totalPagado).max(BigDecimal.ZERO));
        resumen.put("estado", estado);
        return resumen;
    }
}
