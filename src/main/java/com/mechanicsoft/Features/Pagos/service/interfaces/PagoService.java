package com.mechanicsoft.Features.Pagos.service.interfaces;

import com.mechanicsoft.Features.Pagos.entity.Pago;

import java.util.List;
import java.util.Map;

public interface PagoService {

    Pago registrar(Long ordenId, Pago pago);

    List<Pago> listarPorOrden(Long ordenId);

    Pago anular(Long ordenId, Long pagoId);

    Map<String, Object> resumenPago(Long ordenId);

}
