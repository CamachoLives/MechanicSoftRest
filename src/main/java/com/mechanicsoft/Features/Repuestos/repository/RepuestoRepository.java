package com.mechanicsoft.Features.Repuestos.repository;

import com.mechanicsoft.Features.Repuestos.entity.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {

    boolean existsByCodigo(String codigo);

    @Query("SELECT r FROM Repuesto r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "OR LOWER(r.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Repuesto> buscar(@Param("texto") String texto);
}
