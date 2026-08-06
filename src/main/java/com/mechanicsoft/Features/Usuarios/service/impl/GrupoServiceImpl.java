package com.mechanicsoft.Features.Usuarios.service.impl;

import com.mechanicsoft.Features.Usuarios.entity.Grupo;
import com.mechanicsoft.Features.Usuarios.entity.Rol;
import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import com.mechanicsoft.Features.Usuarios.repository.GrupoRepository;
import com.mechanicsoft.Features.Usuarios.repository.RolRepository;
import com.mechanicsoft.Features.Usuarios.repository.UsuarioRepository;
import com.mechanicsoft.Features.Usuarios.service.interfaces.GrupoService;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GrupoServiceImpl implements GrupoService {

    private final GrupoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public GrupoServiceImpl(GrupoRepository repository, UsuarioRepository usuarioRepository,
                             RolRepository rolRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public Grupo crear(Grupo grupo) {
        grupo.setId(null);
        grupo.setMiembros(resolverMiembros(grupo));
        grupo.setRolBonus(resolverRolBonus(grupo));
        return repository.save(grupo);
    }

    @Override
    public List<Grupo> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<Grupo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Grupo actualizar(Long id, Grupo grupo) {
        Grupo existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado"));

        existente.setNombre(grupo.getNombre());
        existente.setDescripcion(grupo.getDescripcion());
        existente.setMiembros(resolverMiembros(grupo));
        existente.setRolBonus(resolverRolBonus(grupo));

        return repository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Grupo no encontrado");
        }
        repository.deleteById(id);
    }

    private List<Usuario> resolverMiembros(Grupo grupo) {
        if (grupo.getMiembros() == null || grupo.getMiembros().isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> ids = grupo.getMiembros().stream().map(Usuario::getId).toList();
        return usuarioRepository.findAllById(ids);
    }

    private Rol resolverRolBonus(Grupo grupo) {
        if (grupo.getRolBonus() == null || grupo.getRolBonus().getId() == null) {
            return null;
        }
        return rolRepository.findById(grupo.getRolBonus().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol bono no encontrado"));
    }
}
