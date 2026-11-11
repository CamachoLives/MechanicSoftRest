package com.mechanicsoft.Features.ConsultaPublica.dto;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ConsultaResponse(
    VehiculoInfo vehiculo,
    List<OrdenInfo> ordenes
) {

    public record VehiculoInfo(
        String placa,
        String marca,
        String modelo,
        Integer anio,
        String color,
        Integer kilometraje,
        Integer cilindrajeCc
    ) {
        public static VehiculoInfo desde(Vehiculo v) {
            return new VehiculoInfo(v.getPlaca(), v.getMarca(), v.getModelo(),
                    v.getAnio(), v.getColor(), v.getKilometraje(), v.getCilindrajeCc());
        }
    }

    public record OrdenInfo(
        Long id,
        String estado,
        String estadoEtiqueta,
        String motivoIngreso,
        LocalDateTime fechaIngreso,
        String diagnostico,
        String trabajoRealizado,
        String observaciones,
        BigDecimal valorTotal,
        LocalDateTime fechaEntrega
    ) {
        private static final Map<EstadoOrden, String> ETIQUETAS = Map.of(
            EstadoOrden.RECIBIDO,           "Recibida",
            EstadoOrden.EN_DIAGNOSTICO,     "En diagnóstico",
            EstadoOrden.ESPERA_APROBACION,  "Esperando aprobación",
            EstadoOrden.APROBADO,           "Aprobada",
            EstadoOrden.EN_REPARACION,      "En reparación",
            EstadoOrden.FINALIZADO,         "Finalizada",
            EstadoOrden.ENTREGADO,          "Entregada",
            EstadoOrden.CANCELADO,          "Cancelada"
        );

        public static OrdenInfo desde(OrdenServicio o) {
            return new OrdenInfo(
                o.getId(),
                o.getEstado().name(),
                ETIQUETAS.getOrDefault(o.getEstado(), o.getEstado().name()),
                o.getMotivoIngreso(),
                o.getFechaIngreso(),
                o.getDiagnostico(),
                o.getTrabajoRealizado(),
                o.getObservaciones(),
                o.getValorTotal(),
                o.getFechaEntrega()
            );
        }
    }
}
