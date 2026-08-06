package com.mechanicsoft.Features.Usuarios.service.interfaces;

import com.mechanicsoft.Features.Usuarios.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Usuario crear(Usuario usuario);

    List<Usuario> listar();

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsuario(String usuario);

    /**
     * @param nuevaContrasena texto plano opcional; si es null o vacío, no cambia la contraseña actual.
     */
    Usuario actualizar(Long id, Usuario datos, String nuevaContrasena);

    Usuario cambiarEstado(Long id, boolean activo);

}
