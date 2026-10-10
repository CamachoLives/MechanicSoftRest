package com.mechanicsoft.Features.OrdenesServicio.repository;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.dto.EstadoOrdenResumen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Long> {

    // Toda la serialización de OrdenServicio expone vehiculo.cliente (placa,
    // nombre del dueño, etc.), así que sin este @EntityGraph cada fila del
    // listado dispara dos SELECT extra al forzar la carga perezosa
    // (vehiculo, y luego vehiculo.cliente) — un N+1 real con listas largas.
    @Override
    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    List<OrdenServicio> findAll();

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    List<OrdenServicio> findByEstado(EstadoOrden estado);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    List<OrdenServicio> findByVehiculoId(Long vehiculoId);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    List<OrdenServicio> findByVehiculo_Cliente_Id(Long clienteId);

    @Query("SELECT new com.mechanicsoft.Features.OrdenesServicio.dto.EstadoOrdenResumen(o.estado, COUNT(o)) " +
            "FROM OrdenServicio o GROUP BY o.estado")
    List<EstadoOrdenResumen> resumirPorEstado();

}
