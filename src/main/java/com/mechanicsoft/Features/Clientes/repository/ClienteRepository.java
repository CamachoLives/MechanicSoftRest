package com.mechanicsoft.Features.Clientes.repository;

import com.mechanicsoft.Features.Clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByTelefono(String telefono);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "OR c.telefono LIKE CONCAT('%', :texto, '%')")
    List<Cliente> buscar(@Param("texto") String texto);
}
