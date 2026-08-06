package com.mechanicsoft.Features.Usuarios.service.impl;

import com.mechanicsoft.Features.Usuarios.entity.Rol;
import com.mechanicsoft.Features.Usuarios.repository.GrupoRepository;
import com.mechanicsoft.Features.Usuarios.repository.RolRepository;
import com.mechanicsoft.Features.Usuarios.repository.UsuarioRepository;
import com.mechanicsoft.Features.Usuarios.service.interfaces.RolService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import com.mechanicsoft.exception.TransicionInvalidaException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;

    public RolServiceImpl(RolRepository repository, UsuarioRepository usuarioRepository,
                           GrupoRepository grupoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.grupoRepository = grupoRepository;
    }

    @Override
    public Rol crear(Rol rol) {
        if (repository.existsByNombre(rol.getNombre())) {
            throw new TransicionInvalidaException("Ya existe un rol con ese nombre.");
        }
        rol.setId(null);
        rol.setEsSistema(false);
        return repository.save(rol);
    }

    @Override
    public List<Rol> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<Rol> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Rol actualizar(Long id, Rol rol) {
        Rol existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));

        existente.setNombre(rol.getNombre());
        existente.setDescripcion(rol.getDescripcion());
        existente.setPermisos(rol.getPermisos());
        existente.setColorBadge(rol.getColorBadge());
        // esSistema no se toca desde aquí: bloquea el borrado, no la edición de permisos.

        return repository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Rol rol = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));

        if (Boolean.TRUE.equals(rol.getEsSistema())) {
            throw new TransicionInvalidaException("Este rol es del sistema y no puede eliminarse.");
        }
        boolean tieneUsuariosAsignados = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRol() != null && u.getRol().getId().equals(id));
        if (tieneUsuariosAsignados) {
            throw new TransicionInvalidaException("No se puede eliminar: hay usuarios con este rol asignado.");
        }
        boolean esRolBonusDeAlgunGrupo = grupoRepository.findAll().stream()
                .anyMatch(g -> g.getRolBonus() != null && g.getRolBonus().getId().equals(id));
        if (esRolBonusDeAlgunGrupo) {
            throw new TransicionInvalidaException("No se puede eliminar: hay grupos que otorgan este rol como bono.");
        }

        repository.deleteById(id);
    }
}
