package com.mechanicsoft.Features.OrdenesServicio.entity;

import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordenes_servicio", indexes = @Index(name = "idx_ordenes_vehiculo_id", columnList = "vehiculo_id"))
public class OrdenServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El vehículo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @NotNull(message = "El kilometraje de ingreso es obligatorio")
    @Min(0)
    @Column(name = "kilometraje_ingreso", nullable = false)
    private Integer kilometrajeIngreso;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    @NotBlank(message = "El motivo de ingreso es obligatorio")
    @Column(name = "motivo_ingreso", nullable = false, length = 500)
    private String motivoIngreso;

    @Column(length = 1000)
    private String diagnostico;

    @Column(name = "trabajo_realizado", length = 1000)
    private String trabajoRealizado;

    @Column(length = 500)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoOrden estado = EstadoOrden.RECIBIDO;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "orden_mecanicos",
            joinColumns = @JoinColumn(name = "orden_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> mecanicos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        fechaIngreso = LocalDateTime.now();
        createdAt = fechaIngreso;
        updatedAt = fechaIngreso;
        if (estado == null) {
            estado = EstadoOrden.RECIBIDO;
        }
        if (valorTotal == null) {
            valorTotal = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
