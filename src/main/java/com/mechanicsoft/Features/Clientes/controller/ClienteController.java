package com.mechanicsoft.Features.Clientes.controller;

import com.mechanicsoft.Features.Clientes.entity.Cliente;
import com.mechanicsoft.Features.Clientes.entity.TipoDocumento;
import com.mechanicsoft.Features.Clientes.service.interfaces.ClienteService;
import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import com.mechanicsoft.Features.Vehiculos.service.interfaces.VehiculoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;
    private final VehiculoService vehiculoService;

    public ClienteController(ClienteService service, VehiculoService vehiculoService) {
        this.service = service;
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente guardar(@Valid @RequestBody Cliente cliente) {
        return service.guardar(cliente);
    }

    @GetMapping
    public List<Cliente> listar(@RequestParam(required = false) String buscar) {
        return service.listar(buscar);
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
    }

    @GetMapping("/por-documento/{tipo}/{numero}")
    public ResponseEntity<Cliente> buscarPorDocumento(@PathVariable TipoDocumento tipo,
                                                       @PathVariable String numero) {
        return service.buscarPorDocumento(tipo, numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return service.actualizar(id, cliente);
    }

    @PatchMapping("/{id}/estado")
    public Cliente cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return service.cambiarEstado(id, Boolean.TRUE.equals(body.get("activo")));
    }

    @GetMapping("/{id}/vehiculos")
    public List<Vehiculo> vehiculosDelCliente(@PathVariable Long id) {
        return vehiculoService.listarPorCliente(id);
    }
}
