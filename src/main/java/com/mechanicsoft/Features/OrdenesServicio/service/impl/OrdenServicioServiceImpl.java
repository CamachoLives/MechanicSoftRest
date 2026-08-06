package com.mechanicsoft.Features.OrdenesServicio.service.impl;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.repository.OrdenServicioRepository;
import com.mechanicsoft.Features.OrdenesServicio.service.interfaces.OrdenServicioService;
import com.mechanicsoft.Features.RepuestosOrden.entity.RepuestoOrden;
import com.mechanicsoft.Features.RepuestosOrden.repository.RepuestoOrdenRepository;
import com.mechanicsoft.Features.ServiciosOrden.entity.ServicioOrden;
import com.mechanicsoft.Features.ServiciosOrden.repository.ServicioOrdenRepository;
import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import com.mechanicsoft.Features.Usuarios.repository.UsuarioRepository;
import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import com.mechanicsoft.Features.Vehiculos.repository.VehiculoRepository;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import com.mechanicsoft.exception.TransicionInvalidaException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class OrdenServicioServiceImpl implements OrdenServicioService {

    /** A qué estados puede pasar una orden desde cada estado actual. */
    private static final Map<EstadoOrden, Set<EstadoOrden>> TRANSICIONES_PERMITIDAS = Map.of(
            EstadoOrden.RECIBIDO, Set.of(EstadoOrden.EN_DIAGNOSTICO, EstadoOrden.CANCELADO),
            EstadoOrden.EN_DIAGNOSTICO, Set.of(EstadoOrden.ESPERA_APROBACION, EstadoOrden.CANCELADO),
            EstadoOrden.ESPERA_APROBACION, Set.of(EstadoOrden.APROBADO, EstadoOrden.EN_DIAGNOSTICO, EstadoOrden.CANCELADO),
            EstadoOrden.APROBADO, Set.of(EstadoOrden.EN_REPARACION, EstadoOrden.CANCELADO),
            EstadoOrden.EN_REPARACION, Set.of(EstadoOrden.FINALIZADO, EstadoOrden.ESPERA_APROBACION, EstadoOrden.CANCELADO),
            EstadoOrden.FINALIZADO, Set.of(EstadoOrden.ENTREGADO, EstadoOrden.EN_REPARACION),
            EstadoOrden.ENTREGADO, Set.of(),
            EstadoOrden.CANCELADO, Set.of()
    );

    /** Estados en los que ya no se pueden agregar/quitar líneas de servicio, repuesto o pago. */
    private static final Set<EstadoOrden> ESTADOS_INMUTABLES =
            Set.of(EstadoOrden.FINALIZADO, EstadoOrden.ENTREGADO, EstadoOrden.CANCELADO);

    private final OrdenServicioRepository repository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioOrdenRepository servicioOrdenRepository;
    private final RepuestoOrdenRepository repuestoOrdenRepository;

    public OrdenServicioServiceImpl(OrdenServicioRepository repository, VehiculoRepository vehiculoRepository,
                                     UsuarioRepository usuarioRepository, ServicioOrdenRepository servicioOrdenRepository,
                                     RepuestoOrdenRepository repuestoOrdenRepository) {
        this.repository = repository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioOrdenRepository = servicioOrdenRepository;
        this.repuestoOrdenRepository = repuestoOrdenRepository;
    }

    @Override
    public OrdenServicio crear(OrdenServicio orden) {
        orden.setId(null);
        orden.setVehiculo(resolverVehiculo(orden));
        orden.setMecanicos(resolverMecanicos(orden));
        orden.setEstado(EstadoOrden.RECIBIDO);
        orden.setValorTotal(BigDecimal.ZERO);
        orden.setFechaEntrega(null);
        orden.setDiagnostico(null);
        orden.setTrabajoRealizado(null);
        return repository.save(orden);
    }

    @Override
    public List<OrdenServicio> listar(EstadoOrden estado, Long vehiculoId, Long clienteId) {
        if (vehiculoId != null) {
            return repository.findByVehiculoId(vehiculoId);
        }
        if (clienteId != null) {
            return repository.findByVehiculo_Cliente_Id(clienteId);
        }
        if (estado != null) {
            return repository.findByEstado(estado);
        }
        return repository.findAll();
    }

    @Override
    public Optional<OrdenServicio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public OrdenServicio obtenerOrden(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de servicio no encontrada"));
    }

    @Override
    public OrdenServicio actualizar(Long id, OrdenServicio datos) {
        OrdenServicio existente = obtenerOrden(id);
        verificarMutable(existente);

        existente.setDiagnostico(datos.getDiagnostico());
        existente.setTrabajoRealizado(datos.getTrabajoRealizado());
        existente.setObservaciones(datos.getObservaciones());
        existente.setMecanicos(resolverMecanicos(datos));

        return repository.save(existente);
    }

    @Override
    public OrdenServicio cambiarEstado(Long id, EstadoOrden nuevoEstado) {
        OrdenServicio existente = obtenerOrden(id);

        Set<EstadoOrden> permitidos = TRANSICIONES_PERMITIDAS.getOrDefault(existente.getEstado(), Set.of());
        if (!permitidos.contains(nuevoEstado)) {
            throw new TransicionInvalidaException(
                    "No se puede pasar de " + existente.getEstado() + " a " + nuevoEstado + ".");
        }

        existente.setEstado(nuevoEstado);
        existente.setFechaEntrega(nuevoEstado == EstadoOrden.ENTREGADO ? LocalDateTime.now() : existente.getFechaEntrega());

        return repository.save(existente);
    }

    @Override
    public void verificarMutable(OrdenServicio orden) {
        if (ESTADOS_INMUTABLES.contains(orden.getEstado())) {
            throw new TransicionInvalidaException(
                    "Esta orden ya no admite cambios (estado: " + orden.getEstado() + ").");
        }
    }

    @Override
    public void recalcularValorTotal(Long ordenId) {
        OrdenServicio orden = obtenerOrden(ordenId);

        BigDecimal totalServicios = servicioOrdenRepository.findByOrdenId(ordenId).stream()
                .map(ServicioOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRepuestos = repuestoOrdenRepository.findByOrdenId(ordenId).stream()
                .map(RepuestoOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orden.setValorTotal(totalServicios.add(totalRepuestos));
        repository.save(orden);
    }

    private Vehiculo resolverVehiculo(OrdenServicio orden) {
        if (orden.getVehiculo() == null || orden.getVehiculo().getId() == null) {
            throw new RecursoNoEncontradoException("La orden debe tener un vehículo asociado");
        }
        return vehiculoRepository.findById(orden.getVehiculo().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));
    }

    private List<Usuario> resolverMecanicos(OrdenServicio orden) {
        if (orden.getMecanicos() == null || orden.getMecanicos().isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> ids = orden.getMecanicos().stream().map(Usuario::getId).toList();
        return usuarioRepository.findAllById(ids);
    }
}
