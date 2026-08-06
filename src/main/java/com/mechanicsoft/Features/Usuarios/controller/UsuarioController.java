package com.mechanicsoft.Features.Usuarios.controller;

import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import com.mechanicsoft.Features.Usuarios.service.interfaces.UsuarioService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public Usuario crear(@Valid @RequestBody Usuario usuario) {
        return service.crear(usuario);
    }

    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        return service.actualizar(id, usuario, usuario.getContrasena());
    }

    @PatchMapping("/{id}/estado")
    public Usuario cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return service.cambiarEstado(id, Boolean.TRUE.equals(body.get("activo")));
    }
}
