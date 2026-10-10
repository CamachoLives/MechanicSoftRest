package com.mechanicsoft.Features.OrdenesServicio.controller;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.dto.ResumenOrdenesResponse;
import com.mechanicsoft.Features.OrdenesServicio.service.interfaces.OrdenServicioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenServicioController {

    private final OrdenServicioService service;

    public OrdenServicioController(OrdenServicioService service) {
        this.service = service;
    }

    @PostMapping
    public OrdenServicio crear(@Valid @RequestBody OrdenServicio orden) {
        return service.crear(orden);
    }

    @GetMapping
    public List<OrdenServicio> listar(
            @RequestParam(required = false) EstadoOrden estado,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) Long clienteId
    ) {
        return service.listar(estado, vehiculoId, clienteId);
    }

    @GetMapping("/resumen")
    public ResumenOrdenesResponse resumir() {
        return service.resumir();
    }

    @GetMapping("/{id}")
    public OrdenServicio buscarPorId(@PathVariable Long id) {
        return service.obtenerOrden(id);
    }

    @PutMapping("/{id}")
    public OrdenServicio actualizar(@PathVariable Long id, @RequestBody OrdenServicio orden) {
        return service.actualizar(id, orden);
    }

    @PatchMapping("/{id}/estado")
    public OrdenServicio cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        EstadoOrden nuevoEstado = EstadoOrden.valueOf(estado);
        return service.cambiarEstado(id, nuevoEstado);
    }
}
