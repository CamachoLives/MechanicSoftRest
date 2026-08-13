package com.mechanicsoft.Features.RepuestosOrden.entity;

import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.Repuestos.entity.Repuesto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repuestos_orden", indexes = {
        @Index(name = "idx_repuestos_orden_orden_id", columnList = "orden_id"),
        @Index(name = "idx_repuestos_orden_repuesto_id", columnList = "repuesto_id")
})
public class RepuestoOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sin @NotNull: la asigna el controlador a partir de la URL (/ordenes/{ordenId}/repuestos),
    // nunca la manda el cliente en el cuerpo de la petición.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    // El cliente ya sabe a qué orden pertenece esta línea (viene en la URL);
    // serializarla completa duplicaría vehiculo+cliente+mecánicos en cada fila.
    @JsonIgnore
    private OrdenServicio orden;

    @NotNull(message = "El repuesto es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "repuesto_id", nullable = false)
    private Repuesto repuesto;

    @NotNull
    @Min(1)
    @Column(name = "cantidad_utilizada", nullable = false)
    private Integer cantidadUtilizada;

    // Precio "congelado" al momento de usar el repuesto: si el precio del
    // catálogo cambia después, el total histórico de esta orden no se altera.
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
