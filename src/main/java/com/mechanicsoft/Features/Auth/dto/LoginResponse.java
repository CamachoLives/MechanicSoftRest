package com.mechanicsoft.Features.Auth.dto;

import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {

    private Long id;
    private String usuario;
    private String nombre;
    private String correo;
    private Long rolId;
    private String rolNombre;
    private List<String> permisos;

    public static LoginResponse desde(Usuario usuario) {
        LoginResponse respuesta = new LoginResponse();
        respuesta.setId(usuario.getId());
        respuesta.setUsuario(usuario.getUsuario());
        respuesta.setNombre(usuario.getNombre());
        respuesta.setCorreo(usuario.getCorreo());
        if (usuario.getRol() != null) {
            respuesta.setRolId(usuario.getRol().getId());
            respuesta.setRolNombre(usuario.getRol().getNombre());
            respuesta.setPermisos(usuario.getRol().getPermisos());
        }
        return respuesta;
    }
}
