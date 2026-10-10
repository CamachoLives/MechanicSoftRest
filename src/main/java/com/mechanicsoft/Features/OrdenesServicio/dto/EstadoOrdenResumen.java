package com.mechanicsoft.Features.OrdenesServicio.dto;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;

public record EstadoOrdenResumen(EstadoOrden estado, long cantidad) {
}
