package com.mechanicsoft.Features.Pagos.entity;

import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sin @NotNull: la asigna el controlador a partir de la URL (/ordenes/{ordenId}/pagos),
    // nunca la manda el cliente en el cuerpo de la petición.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    // El cliente ya sabe a qué orden pertenece esta línea (viene en la URL);
    // serializarla completa duplicaría vehiculo+cliente+mecánicos en cada fila.
    @JsonIgnore
    private OrdenServicio orden;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PAGADO;

    @PrePersist
    public void prePersist() {
        fechaPago = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoPago.PAGADO;
        }
    }
}
