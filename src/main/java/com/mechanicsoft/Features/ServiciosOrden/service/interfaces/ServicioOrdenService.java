package com.mechanicsoft.Features.ServiciosOrden.service.interfaces;

import com.mechanicsoft.Features.ServiciosOrden.entity.ServicioOrden;

import java.util.List;

public interface ServicioOrdenService {

    ServicioOrden agregar(Long ordenId, ServicioOrden servicio);

    List<ServicioOrden> listarPorOrden(Long ordenId);

    void eliminar(Long ordenId, Long lineaId);

}
