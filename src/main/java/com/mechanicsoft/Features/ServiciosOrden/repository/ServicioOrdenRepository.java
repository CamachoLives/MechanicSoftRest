package com.mechanicsoft.Features.ServiciosOrden.repository;

import com.mechanicsoft.Features.ServiciosOrden.entity.ServicioOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioOrdenRepository extends JpaRepository<ServicioOrden, Long> {

    List<ServicioOrden> findByOrdenId(Long ordenId);

}
