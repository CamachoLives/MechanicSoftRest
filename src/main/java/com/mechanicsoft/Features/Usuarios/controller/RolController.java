package com.mechanicsoft.Features.Usuarios.controller;

import com.mechanicsoft.Features.Usuarios.data.CatalogoPermisos;
import com.mechanicsoft.Features.Usuarios.entity.Rol;
import com.mechanicsoft.Features.Usuarios.service.interfaces.RolService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService service;

    public RolController(RolService service) {
        this.service = service;
    }

    @PostMapping
    public Rol crear(@Valid @RequestBody Rol rol) {
        return service.crear(rol);
    }

    @GetMapping
    public List<Rol> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Rol buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));
    }

    @PutMapping("/{id}")
    public Rol actualizar(@PathVariable Long id, @Valid @RequestBody Rol rol) {
        return service.actualizar(id, rol);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/permisos")
    public List<String> catalogoPermisos() {
        return CatalogoPermisos.TODOS;
    }
}
