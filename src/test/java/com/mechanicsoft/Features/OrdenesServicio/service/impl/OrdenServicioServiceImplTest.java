package com.mechanicsoft.Features.OrdenesServicio.service.impl;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.dto.EstadoOrdenResumen;
import com.mechanicsoft.Features.OrdenesServicio.repository.OrdenServicioRepository;
import com.mechanicsoft.Features.RepuestosOrden.entity.RepuestoOrden;
import com.mechanicsoft.Features.RepuestosOrden.repository.RepuestoOrdenRepository;
import com.mechanicsoft.Features.ServiciosOrden.entity.ServicioOrden;
import com.mechanicsoft.Features.ServiciosOrden.repository.ServicioOrdenRepository;
import com.mechanicsoft.Features.Usuarios.repository.UsuarioRepository;
import com.mechanicsoft.Features.Vehiculos.repository.VehiculoRepository;
import com.mechanicsoft.exception.TransicionInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * La máquina de estados de OrdenServicio es el corazón de Fase 1 (la "regla
 * fundamental" del flujo de taller) y hasta ahora solo se había verificado a
 * mano con curl. Estas pruebas fijan el comportamiento esperado en código.
 */
class OrdenServicioServiceImplTest {

    private OrdenServicioRepository repository;
    private ServicioOrdenRepository servicioOrdenRepository;
    private RepuestoOrdenRepository repuestoOrdenRepository;
    private OrdenServicioServiceImpl service;

    @BeforeEach
    void configurar() {
        repository = Mockito.mock(OrdenServicioRepository.class);
        VehiculoRepository vehiculoRepository = Mockito.mock(VehiculoRepository.class);
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        servicioOrdenRepository = Mockito.mock(ServicioOrdenRepository.class);
        repuestoOrdenRepository = Mockito.mock(RepuestoOrdenRepository.class);
        service = new OrdenServicioServiceImpl(
                repository, vehiculoRepository, usuarioRepository, servicioOrdenRepository, repuestoOrdenRepository);

        when(repository.save(any(OrdenServicio.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private OrdenServicio ordenEnEstado(EstadoOrden estado) {
        OrdenServicio orden = new OrdenServicio();
        orden.setId(1L);
        orden.setEstado(estado);
        when(repository.findById(1L)).thenReturn(Optional.of(orden));
        return orden;
    }

    @ParameterizedTest(name = "{0} -> {1} es una transición permitida")
    @CsvSource({
            "RECIBIDO, EN_DIAGNOSTICO",
            "RECIBIDO, CANCELADO",
            "EN_DIAGNOSTICO, ESPERA_APROBACION",
            "ESPERA_APROBACION, APROBADO",
            "ESPERA_APROBACION, EN_DIAGNOSTICO",
            "APROBADO, EN_REPARACION",
            "EN_REPARACION, FINALIZADO",
            "EN_REPARACION, ESPERA_APROBACION",
            "FINALIZADO, ENTREGADO",
            "FINALIZADO, EN_REPARACION",
    })
    void permiteLasTransicionesDefinidas(EstadoOrden desde, EstadoOrden hasta) {
        ordenEnEstado(desde);
        OrdenServicio resultado = service.cambiarEstado(1L, hasta);
        assertEquals(hasta, resultado.getEstado());
    }

    @ParameterizedTest(name = "{0} -> {1} debe rechazarse")
    @CsvSource({
            "RECIBIDO, APROBADO",
            "RECIBIDO, ENTREGADO",
            "EN_DIAGNOSTICO, APROBADO",
            "APROBADO, ESPERA_APROBACION",
            "ENTREGADO, EN_REPARACION",
            "ENTREGADO, RECIBIDO",
            "CANCELADO, RECIBIDO",
            "FINALIZADO, CANCELADO",
    })
    void rechazaTransicionesNoDefinidas(EstadoOrden desde, EstadoOrden hasta) {
        ordenEnEstado(desde);
        assertThrows(TransicionInvalidaException.class, () -> service.cambiarEstado(1L, hasta));
    }

    @Test
    void marcaFechaDeEntregaSoloAlLlegarAEntregado() {
        ordenEnEstado(EstadoOrden.FINALIZADO);
        OrdenServicio resultado = service.cambiarEstado(1L, EstadoOrden.ENTREGADO);
        assertEquals(EstadoOrden.ENTREGADO, resultado.getEstado());
        org.junit.jupiter.api.Assertions.assertNotNull(resultado.getFechaEntrega());
    }

    @Test
    void verificarMutable_permiteOrdenesActivas() {
        OrdenServicio orden = new OrdenServicio();
        orden.setEstado(EstadoOrden.EN_REPARACION);
        service.verificarMutable(orden); // no debe lanzar
    }

    @Test
    void verificarMutable_rechazaEstadosTerminales() {
        for (EstadoOrden estado : List.of(EstadoOrden.FINALIZADO, EstadoOrden.ENTREGADO, EstadoOrden.CANCELADO)) {
            OrdenServicio orden = new OrdenServicio();
            orden.setEstado(estado);
            assertThrows(TransicionInvalidaException.class, () -> service.verificarMutable(orden));
        }
    }

    @Test
    void recalcularValorTotal_sumaServiciosYRepuestos() {
        OrdenServicio orden = ordenEnEstado(EstadoOrden.EN_REPARACION);

        ServicioOrden servicio = new ServicioOrden();
        servicio.setSubtotal(new BigDecimal("30000"));
        when(servicioOrdenRepository.findByOrdenId(1L)).thenReturn(List.of(servicio));

        RepuestoOrden repuesto = new RepuestoOrden();
        repuesto.setSubtotal(new BigDecimal("50000"));
        when(repuestoOrdenRepository.findByOrdenId(1L)).thenReturn(List.of(repuesto));

        service.recalcularValorTotal(1L);

        assertEquals(0, new BigDecimal("80000").compareTo(orden.getValorTotal()));
    }

    @Test
    void resumir_incluyeTodosLosEstadosYCeroParaLosSinOrdenes() {
        when(repository.resumirPorEstado()).thenReturn(List.of(
                new EstadoOrdenResumen(EstadoOrden.RECIBIDO, 2),
                new EstadoOrdenResumen(EstadoOrden.FINALIZADO, 1),
                new EstadoOrdenResumen(EstadoOrden.ENTREGADO, 3),
                new EstadoOrdenResumen(EstadoOrden.CANCELADO, 1)
        ));

        var resumen = service.resumir();

        assertEquals(7, resumen.totalOrdenes());
        assertEquals(3, resumen.ordenesAbiertas());
        assertEquals(List.of(EstadoOrden.values()), resumen.porEstado().stream()
                .map(EstadoOrdenResumen::estado)
                .toList());
        assertEquals(0, resumen.porEstado().stream()
                .filter(estado -> estado.estado() == EstadoOrden.EN_DIAGNOSTICO)
                .findFirst()
                .orElseThrow()
                .cantidad());
    }
}
