package com.mechanicsoft.Features.RepuestosOrden.service.interfaces;

import com.mechanicsoft.Features.RepuestosOrden.entity.RepuestoOrden;

import java.util.List;

public interface RepuestoOrdenService {

    RepuestoOrden agregar(Long ordenId, RepuestoOrden linea);

    List<RepuestoOrden> listarPorOrden(Long ordenId);

    void eliminar(Long ordenId, Long lineaId);

}
