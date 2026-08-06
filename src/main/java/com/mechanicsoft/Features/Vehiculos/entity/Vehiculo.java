package com.mechanicsoft.Features.Vehiculos.entity;

import com.mechanicsoft.Features.Clientes.entity.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La placa es obligatoria")
    @Column(nullable = false, unique = true, length = 20)
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false, length = 100)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Column(nullable = false, length = 100)
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Column(nullable = false)
    private Integer anio;

    @NotBlank(message = "El color es obligatorio")
    @Column(nullable = false, length = 50)
    private String color;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer kilometraje;

    @NotNull
    @Min(50)
    @Column(name = "cilindraje_cc", nullable = false)
    private Integer cilindrajeCc;

    @Column(name = "foto_vehiculo")
    private String fotoVehiculo;

    @Column(length = 500)
    private String observaciones;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
