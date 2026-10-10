package com.mechanicsoft.Features.OrdenesServicio.service.interfaces;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.dto.ResumenOrdenesResponse;

import java.util.List;
import java.util.Optional;

public interface OrdenServicioService {

    OrdenServicio crear(OrdenServicio orden);

    List<OrdenServicio> listar(EstadoOrden estado, Long vehiculoId, Long clienteId);

    ResumenOrdenesResponse resumir();

    Optional<OrdenServicio> buscarPorId(Long id);

    OrdenServicio obtenerOrden(Long id);

    OrdenServicio actualizar(Long id, OrdenServicio datos);

    OrdenServicio cambiarEstado(Long id, EstadoOrden nuevoEstado);

    /** Lanza TransicionInvalidaException si la orden ya no admite líneas/pagos nuevos. */
    void verificarMutable(OrdenServicio orden);

    /** Recalcula valorTotal sumando servicios + repuestos de la orden y la guarda. */
    void recalcularValorTotal(Long ordenId);

}
