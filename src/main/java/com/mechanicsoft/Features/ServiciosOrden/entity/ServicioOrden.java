package com.mechanicsoft.Features.ServiciosOrden.entity;

import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "servicios_orden")
public class ServicioOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sin @NotNull: la asigna el controlador a partir de la URL (/ordenes/{ordenId}/servicios),
    // nunca la manda el cliente en el cuerpo de la petición.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    // El cliente ya sabe a qué orden pertenece esta línea (viene en la URL);
    // serializarla completa duplicaría vehiculo+cliente+mecánicos en cada fila.
    @JsonIgnore
    private OrdenServicio orden;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Column(name = "nombre_servicio", nullable = false, length = 150)
    private String nombreServicio;

    @Column(length = 300)
    private String descripcion;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
