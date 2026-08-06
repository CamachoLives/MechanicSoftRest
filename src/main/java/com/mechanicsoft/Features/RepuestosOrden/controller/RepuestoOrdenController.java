package com.mechanicsoft.Features.RepuestosOrden.controller;

import com.mechanicsoft.Features.RepuestosOrden.entity.RepuestoOrden;
import com.mechanicsoft.Features.RepuestosOrden.service.interfaces.RepuestoOrdenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes/{ordenId}/repuestos")
public class RepuestoOrdenController {

    private final RepuestoOrdenService service;

    public RepuestoOrdenController(RepuestoOrdenService service) {
        this.service = service;
    }

    @PostMapping
    public RepuestoOrden agregar(@PathVariable Long ordenId, @Valid @RequestBody RepuestoOrden linea) {
        return service.agregar(ordenId, linea);
    }

    @GetMapping
    public List<RepuestoOrden> listar(@PathVariable Long ordenId) {
        return service.listarPorOrden(ordenId);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long ordenId, @PathVariable Long id) {
        service.eliminar(ordenId, id);
    }
}
