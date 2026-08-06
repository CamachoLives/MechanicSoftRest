package com.mechanicsoft.Features.OrdenesServicio.repository;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Long> {

    List<OrdenServicio> findByEstado(EstadoOrden estado);

    List<OrdenServicio> findByVehiculoId(Long vehiculoId);

    List<OrdenServicio> findByVehiculo_Cliente_Id(Long clienteId);

}
