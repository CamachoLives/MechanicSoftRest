package com.mechanicsoft.Features.Vehiculos.controller;

import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import com.mechanicsoft.Features.Vehiculos.service.interfaces.VehiculoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService service;

    public VehiculoController(VehiculoService service) {
        this.service = service;
    }

    @PostMapping
    public Vehiculo guardar(@Valid @RequestBody Vehiculo vehiculo) {
        return service.guardar(vehiculo);
    }

    @GetMapping
    public List<Vehiculo> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Vehiculo buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));
    }

    @GetMapping("/placa/{placa}")
    public Vehiculo buscarPorPlaca(@PathVariable String placa) {
        return service.buscarPorPlaca(placa)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));
    }

    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Long id, @Valid @RequestBody Vehiculo vehiculo) {
        return service.actualizar(id, vehiculo);
    }

    @PatchMapping("/{id}/estado")
    public Vehiculo cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return service.cambiarEstado(id, Boolean.TRUE.equals(body.get("activo")));
    }
}
