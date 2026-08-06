package com.mechanicsoft.Features.Usuarios.controller;

import com.mechanicsoft.Features.Usuarios.entity.Grupo;
import com.mechanicsoft.Features.Usuarios.service.interfaces.GrupoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final GrupoService service;

    public GrupoController(GrupoService service) {
        this.service = service;
    }

    @PostMapping
    public Grupo crear(@Valid @RequestBody Grupo grupo) {
        return service.crear(grupo);
    }

    @GetMapping
    public List<Grupo> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Grupo buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado"));
    }

    @PutMapping("/{id}")
    public Grupo actualizar(@PathVariable Long id, @Valid @RequestBody Grupo grupo) {
        return service.actualizar(id, grupo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
