package com.mechanicsoft.Features.RepuestosOrden.repository;

import com.mechanicsoft.Features.RepuestosOrden.entity.RepuestoOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepuestoOrdenRepository extends JpaRepository<RepuestoOrden, Long> {

    List<RepuestoOrden> findByOrdenId(Long ordenId);

}
