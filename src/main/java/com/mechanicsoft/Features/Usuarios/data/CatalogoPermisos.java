package com.mechanicsoft.Features.Usuarios.data;

import java.util.List;

/**
 * Catálogo fijo de permisos del sistema. No es una tabla: los permisos no se
 * crean ni se editan, solo se combinan dentro de cada Rol.
 */
public final class CatalogoPermisos {

    private CatalogoPermisos() {
    }

    public static final List<String> TODOS = List.of(
            "vehiculos.ver", "vehiculos.crear", "vehiculos.editar", "vehiculos.eliminar",
            "clientes.ver", "clientes.crear", "clientes.editar", "clientes.eliminar",
            "ordenes.ver", "ordenes.crear", "ordenes.editar", "ordenes.cambiarEstado",
            "ordenes.gestionarServicios", "ordenes.gestionarRepuestos",
            "repuestos.ver", "repuestos.crear", "repuestos.editar", "repuestos.eliminar", "repuestos.movimientos",
            "pagos.ver", "pagos.registrar", "pagos.anular",
            "usuarios.ver", "usuarios.crear", "usuarios.editar", "usuarios.eliminar",
            "roles.ver", "roles.crear", "roles.editar", "roles.eliminar",
            "grupos.ver", "grupos.crear", "grupos.editar", "grupos.eliminar"
    );
}
