package com.mechanicsoft.Features.Usuarios.service.impl;

import com.mechanicsoft.Features.Usuarios.entity.Rol;
import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import com.mechanicsoft.Features.Usuarios.repository.RolRepository;
import com.mechanicsoft.Features.Usuarios.repository.UsuarioRepository;
import com.mechanicsoft.Features.Usuarios.service.interfaces.UsuarioService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import com.mechanicsoft.exception.TransicionInvalidaException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final String PERMISO_ADMINISTRAR_USUARIOS = "usuarios.editar";

    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository repository, RolRepository rolRepository,
                               PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario crear(Usuario usuario) {
        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
            throw new TransicionInvalidaException("La contraseña es obligatoria.");
        }
        if (repository.existsByUsuario(usuario.getUsuario())) {
            throw new TransicionInvalidaException("Ya existe un usuario con ese nombre de usuario.");
        }

        usuario.setId(null);
        usuario.setRol(resolverRol(usuario));
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        return repository.save(usuario);
    }

    @Override
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return repository.findByUsuario(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario datos, String nuevaContrasena) {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (datos.getUsuario() != null && !datos.getUsuario().equals(existente.getUsuario())
                && repository.existsByUsuario(datos.getUsuario())) {
            throw new TransicionInvalidaException("Ya existe un usuario con ese nombre de usuario.");
        }

        boolean cambiaRol = datos.getRol() != null && datos.getRol().getId() != null
                && !datos.getRol().getId().equals(existente.getRol().getId());
        if (Boolean.FALSE.equals(datos.getActivo()) || cambiaRol) {
            verificarNoUltimoAdministrador(id);
        }

        existente.setUsuario(datos.getUsuario());
        existente.setNombre(datos.getNombre());
        existente.setCorreo(datos.getCorreo());
        existente.setTelefono(datos.getTelefono());
        existente.setCargo(datos.getCargo());
        existente.setRol(resolverRol(datos));
        if (datos.getActivo() != null) {
            existente.setActivo(datos.getActivo());
        }
        if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
            existente.setContrasena(passwordEncoder.encode(nuevaContrasena));
        }

        return repository.save(existente);
    }

    @Override
    public Usuario cambiarEstado(Long id, boolean activo) {
        if (!activo) {
            verificarNoUltimoAdministrador(id);
        }

        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        existente.setActivo(activo);
        return repository.save(existente);
    }

    private Rol resolverRol(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getId() == null) {
            throw new RecursoNoEncontradoException("El usuario debe tener un rol asignado");
        }
        return rolRepository.findById(usuario.getRol().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));
    }

    /** Evita quedarse sin nadie que pueda administrar usuarios. */
    private void verificarNoUltimoAdministrador(Long idAfectado) {
        boolean quedanOtros = repository.findAll().stream()
                .filter(u -> !u.getId().equals(idAfectado) && Boolean.TRUE.equals(u.getActivo()))
                .anyMatch(u -> u.getRol() != null && u.getRol().getPermisos().contains(PERMISO_ADMINISTRAR_USUARIOS));

        if (!quedanOtros) {
            throw new TransicionInvalidaException(
                    "No es posible continuar: no quedaría ningún usuario activo con permiso para administrar usuarios.");
        }
    }
}
