package com.mechanicsoft.Features.Vehiculos.repository;

import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByClienteId(Long clienteId);

}
