package com.mechanicsoft.Features.Pagos.controller;

import com.mechanicsoft.Features.Pagos.entity.Pago;
import com.mechanicsoft.Features.Pagos.service.interfaces.PagoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes/{ordenId}/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @PostMapping
    public Pago registrar(@PathVariable Long ordenId, @Valid @RequestBody Pago pago) {
        return service.registrar(ordenId, pago);
    }

    @GetMapping
    public List<Pago> listar(@PathVariable Long ordenId) {
        return service.listarPorOrden(ordenId);
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen(@PathVariable Long ordenId) {
        return service.resumenPago(ordenId);
    }

    @PatchMapping("/{id}/anular")
    public Pago anular(@PathVariable Long ordenId, @PathVariable Long id) {
        return service.anular(ordenId, id);
    }
}
