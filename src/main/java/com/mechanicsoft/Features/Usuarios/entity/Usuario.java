package com.mechanicsoft.Features.Usuarios.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 100, message = "El usuario no puede tener más de 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String usuario;

    // Sin @NotBlank: es obligatoria al crear (lo valida el servicio), pero al
    // actualizar puede llegar vacía/nula cuando no se quiere cambiar la contraseña.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede tener más de 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Email(message = "El correo no es válido")
    @Size(max = 150, message = "El correo no puede tener más de 150 caracteres")
    @Column(length = 150)
    private String correo;

    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Size(max = 100, message = "El cargo no puede tener más de 100 caracteres")
    @Column(length = 100)
    private String cargo;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }
}
