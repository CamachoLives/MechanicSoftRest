package com.mechanicsoft.Features.RepuestosOrden.service.impl;

import com.mechanicsoft.Features.OrdenesServicio.entity.EstadoOrden;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.service.interfaces.OrdenServicioService;
import com.mechanicsoft.Features.RepuestosOrden.entity.RepuestoOrden;
import com.mechanicsoft.Features.RepuestosOrden.repository.RepuestoOrdenRepository;
import com.mechanicsoft.Features.Repuestos.entity.Repuesto;
import com.mechanicsoft.Features.Repuestos.repository.RepuestoRepository;
import com.mechanicsoft.exception.StockInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El descuento/restauración de stock al agregar o quitar una línea de
 * repuesto de una orden es la parte más fácil de romper de Fase 1 (afecta
 * inventario real). Antes solo se había verificado a mano con curl.
 */
class RepuestoOrdenServiceImplTest {

    private RepuestoOrdenRepository repository;
    private RepuestoRepository repuestoRepository;
    private OrdenServicioService ordenService;
    private RepuestoOrdenServiceImpl service;

    @BeforeEach
    void configurar() {
        repository = Mockito.mock(RepuestoOrdenRepository.class);
        repuestoRepository = Mockito.mock(RepuestoRepository.class);
        ordenService = Mockito.mock(OrdenServicioService.class);
        service = new RepuestoOrdenServiceImpl(repository, repuestoRepository, ordenService);

        when(repository.save(any(RepuestoOrden.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Repuesto repuestoConStock(int cantidad) {
        Repuesto repuesto = new Repuesto();
        repuesto.setId(10L);
        repuesto.setNombre("Filtro de aceite");
        repuesto.setPrecio(new BigDecimal("25000"));
        repuesto.setCantidadDisponible(cantidad);
        when(repuestoRepository.findById(10L)).thenReturn(Optional.of(repuesto));
        return repuesto;
    }

    private OrdenServicio ordenMutable() {
        OrdenServicio orden = new OrdenServicio();
        orden.setId(1L);
        orden.setEstado(EstadoOrden.EN_REPARACION);
        when(ordenService.obtenerOrden(1L)).thenReturn(orden);
        return orden;
    }

    @Test
    void agregar_conStockSuficiente_descuentaStockYCongelaPrecio() {
        ordenMutable();
        Repuesto repuesto = repuestoConStock(10);

        RepuestoOrden linea = new RepuestoOrden();
        linea.setRepuesto(new Repuesto());
        linea.getRepuesto().setId(10L);
        linea.setCantidadUtilizada(3);

        RepuestoOrden resultado = service.agregar(1L, linea);

        assertEquals(7, repuesto.getCantidadDisponible());
        assertEquals(0, new BigDecimal("75000").compareTo(resultado.getSubtotal()));
        assertEquals(0, new BigDecimal("25000").compareTo(resultado.getPrecioUnitario()));
        verify(ordenService).recalcularValorTotal(1L);
    }

    @Test
    void agregar_conStockInsuficiente_lanzaExcepcionYNoDescuenta() {
        ordenMutable();
        Repuesto repuesto = repuestoConStock(2);

        RepuestoOrden linea = new RepuestoOrden();
        linea.setRepuesto(new Repuesto());
        linea.getRepuesto().setId(10L);
        linea.setCantidadUtilizada(5);

        assertThrows(StockInsuficienteException.class, () -> service.agregar(1L, linea));

        assertEquals(2, repuesto.getCantidadDisponible());
        verify(repuestoRepository, never()).save(any());
        verify(ordenService, never()).recalcularValorTotal(any());
    }

    @Test
    void agregar_ordenInmutable_propagaLaValidacionSinTocarStock() {
        OrdenServicio orden = new OrdenServicio();
        orden.setId(1L);
        orden.setEstado(EstadoOrden.ENTREGADO);
        when(ordenService.obtenerOrden(1L)).thenReturn(orden);
        Mockito.doThrow(new com.mechanicsoft.exception.TransicionInvalidaException("inmutable"))
                .when(ordenService).verificarMutable(orden);

        RepuestoOrden linea = new RepuestoOrden();
        linea.setRepuesto(new Repuesto());
        linea.getRepuesto().setId(10L);
        linea.setCantidadUtilizada(1);

        assertThrows(com.mechanicsoft.exception.TransicionInvalidaException.class, () -> service.agregar(1L, linea));
        verify(repuestoRepository, never()).findById(any());
    }

    @Test
    void eliminar_restauraElStockDescartado() {
        OrdenServicio orden = ordenMutable();
        Repuesto repuesto = repuestoConStock(5);

        RepuestoOrden lineaExistente = new RepuestoOrden();
        lineaExistente.setId(99L);
        lineaExistente.setOrden(orden);
        lineaExistente.setRepuesto(repuesto);
        lineaExistente.setCantidadUtilizada(4);
        when(repository.findById(99L)).thenReturn(Optional.of(lineaExistente));

        service.eliminar(1L, 99L);

        assertEquals(9, repuesto.getCantidadDisponible());
        ArgumentCaptor<RepuestoOrden> captor = ArgumentCaptor.forClass(RepuestoOrden.class);
        verify(repository, times(1)).delete(captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(ordenService).recalcularValorTotal(1L);
    }
}
