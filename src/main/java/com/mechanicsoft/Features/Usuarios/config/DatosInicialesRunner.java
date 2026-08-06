package com.mechanicsoft.Features.Usuarios.config;

import com.mechanicsoft.Features.Usuarios.data.CatalogoPermisos;
import com.mechanicsoft.Features.Usuarios.entity.Rol;
import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import com.mechanicsoft.Features.Usuarios.repository.RolRepository;
import com.mechanicsoft.Features.Usuarios.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Siembra los 3 roles de sistema y un usuario administrador la primera vez
 * que arranca la aplicación (base de datos sin usuarios). Sin esto, nadie
 * podría iniciar sesión tras migrar del mock de localStorage al backend real.
 */
@Component
public class DatosInicialesRunner implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosInicialesRunner(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                                 PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Rol administrador = nuevoRol("Administrador",
                "Acceso total: clientes, vehículos, órdenes, repuestos, pagos, usuarios, roles y grupos.",
                CatalogoPermisos.TODOS, "#7dd3fc");

        Rol recepcionista = nuevoRol("Recepcionista",
                "Atiende clientes y vehículos, crea órdenes y registra pagos.",
                List.of(
                        "clientes.ver", "clientes.crear", "clientes.editar",
                        "vehiculos.ver", "vehiculos.crear", "vehiculos.editar",
                        "ordenes.ver", "ordenes.crear", "ordenes.cambiarEstado",
                        "repuestos.ver",
                        "pagos.ver", "pagos.registrar"
                ), "#34d399");

        Rol mecanico = nuevoRol("Mecánico",
                "Trabaja las órdenes: diagnóstico, reparación, servicios y repuestos.",
                List.of(
                        "clientes.ver", "vehiculos.ver",
                        "ordenes.ver", "ordenes.crear", "ordenes.editar", "ordenes.cambiarEstado",
                        "ordenes.gestionarServicios", "ordenes.gestionarRepuestos",
                        "repuestos.ver"
                ), "#60a5fa");

        rolRepository.saveAll(List.of(administrador, recepcionista, mecanico));

        usuarioRepository.save(nuevoUsuario("admin", "admin123", "Laura Gómez",
                "laura.gomez@mechanicsoft.test", administrador));
        usuarioRepository.save(nuevoUsuario("recepcion", "recepcion123", "María Torres",
                "maria.torres@mechanicsoft.test", recepcionista));
        usuarioRepository.save(nuevoUsuario("mecanico", "mecanico123", "Jorge Ramírez",
                "jorge.ramirez@mechanicsoft.test", mecanico));
    }

    private Usuario nuevoUsuario(String usuario, String contrasena, String nombre, String correo, Rol rol) {
        Usuario nuevo = new Usuario();
        nuevo.setUsuario(usuario);
        nuevo.setContrasena(passwordEncoder.encode(contrasena));
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setRol(rol);
        nuevo.setActivo(true);
        return nuevo;
    }

    private Rol nuevoRol(String nombre, String descripcion, List<String> permisos, String colorBadge) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        rol.setPermisos(new ArrayList<>(permisos));
        rol.setColorBadge(colorBadge);
        rol.setEsSistema(true);
        return rol;
    }
}
