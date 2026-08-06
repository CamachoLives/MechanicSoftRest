package com.mechanicsoft.Features.Pagos.repository;

import com.mechanicsoft.Features.Pagos.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByOrdenId(Long ordenId);

}
