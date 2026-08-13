package com.mechanicsoft.Features.Repuestos.controller;

import com.mechanicsoft.Features.Repuestos.entity.Repuesto;
import com.mechanicsoft.Features.Repuestos.service.interfaces.RepuestoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repuestos")
public class RepuestoController {

    private final RepuestoService service;

    public RepuestoController(RepuestoService service) {
        this.service = service;
    }

    @PostMapping
    public Repuesto crear(@Valid @RequestBody Repuesto repuesto) {
        return service.crear(repuesto);
    }

    @GetMapping
    public List<Repuesto> listar(@RequestParam(required = false) String buscar) {
        return service.listar(buscar);
    }

    @GetMapping("/{id}")
    public Repuesto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Repuesto no encontrado"));
    }

    @PutMapping("/{id}")
    public Repuesto actualizar(@PathVariable Long id, @Valid @RequestBody Repuesto repuesto) {
        return service.actualizar(id, repuesto);
    }

    @PatchMapping("/{id}/estado")
    public Repuesto cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return service.cambiarEstado(id, Boolean.TRUE.equals(body.get("activo")));
    }

    @PostMapping("/{id}/entradas")
    public Repuesto registrarEntrada(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return service.registrarEntrada(id, cantidadRequerida(body));
    }

    @PostMapping("/{id}/salidas")
    public Repuesto registrarSalida(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return service.registrarSalida(id, cantidadRequerida(body));
    }

    // getOrDefault solo aplica el default cuando la clave está ausente, no cuando
    // está presente con valor null (ej. {"cantidad": null}) — ese caso llegaba
    // como null hasta el unboxing a "int" del service y explotaba en
    // NullPointerException (500 genérico).
    private int cantidadRequerida(Map<String, Integer> body) {
        Integer cantidad = body.get("cantidad");
        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria.");
        }
        return cantidad;
    }
}
