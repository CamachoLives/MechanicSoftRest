package com.mechanicsoft.Features.Vehiculos.repository;

import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    // Igual que en OrdenServicioRepository: cada vehículo serializado expone
    // su cliente, así que sin el @EntityGraph un listado de N vehículos
    // dispara N SELECT adicionales al forzar la carga perezosa del cliente.
    @Override
    @EntityGraph(attributePaths = {"cliente"})
    List<Vehiculo> findAll();

    @EntityGraph(attributePaths = {"cliente"})
    List<Vehiculo> findByClienteId(Long clienteId);

}
