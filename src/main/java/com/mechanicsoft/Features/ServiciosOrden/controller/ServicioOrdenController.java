package com.mechanicsoft.Features.ServiciosOrden.controller;

import com.mechanicsoft.Features.ServiciosOrden.entity.ServicioOrden;
import com.mechanicsoft.Features.ServiciosOrden.service.interfaces.ServicioOrdenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes/{ordenId}/servicios")
public class ServicioOrdenController {

    private final ServicioOrdenService service;

    public ServicioOrdenController(ServicioOrdenService service) {
        this.service = service;
    }

    @PostMapping
    public ServicioOrden agregar(@PathVariable Long ordenId, @Valid @RequestBody ServicioOrden servicio) {
        return service.agregar(ordenId, servicio);
    }

    @GetMapping
    public List<ServicioOrden> listar(@PathVariable Long ordenId) {
        return service.listarPorOrden(ordenId);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long ordenId, @PathVariable Long id) {
        service.eliminar(ordenId, id);
    }
}
