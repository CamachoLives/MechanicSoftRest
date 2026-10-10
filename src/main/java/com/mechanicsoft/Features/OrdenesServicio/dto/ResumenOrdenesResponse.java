package com.mechanicsoft.Features.OrdenesServicio.dto;

import java.util.List;

public record ResumenOrdenesResponse(long totalOrdenes, long ordenesAbiertas, List<EstadoOrdenResumen> porEstado) {
}
