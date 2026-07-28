# 🔧 MechanicSoft — REST API 🚀

¡Bienvenido al backend de **MechanicSoft**! Esta es una API REST desarrollada en **Spring Boot** para la gestión de talleres de motos, encargada del registro, consulta, actualización y eliminación de los vehículos que ingresan al taller. Trabaja de la mano con el frontend **MechanicSoftIU**, que además añade el tablero del taller y la administración de usuarios, roles y grupos.

---

## ⚡ Características Clave

*   **Gestión de Vehículos:** control detallado de placas, marcas, modelos, kilometraje, cilindraje, fotos y motivo de ingreso al taller, con CRUD completo (incluida la actualización).
*   **Arquitectura Limpia:** Organizado bajo el patrón **Package by Feature**, asegurando que el código sea modular, fácil de mantener y listo para escalar.
*   **Validaciones Rigurosas:** Integración con Jakarta Validation para asegurar que ningún dato corrupto o incompleto llegue a la base de datos.
*   **Auditoría Automática:** Control de tiempos mediante hooks de persistencia (`createdAt`) para saber exactamente cuándo ingresó cada registro.

---

## 🛠️ Stack Tecnológico

El proyecto está construido con las herramientas estándar de la industria para entornos corporativos:

*   **Backend Framework:** Spring Boot 3+
*   **Lenguaje:** Java 17+
*   **Persistencia & ORM:** Spring Data JPA / Hibernate
*   **Base de Datos:** PostgreSQL
*   **Productividad:** Lombok (Getters, Setters, Constructores automáticos)
*   **Seguridad y Validación:** Jakarta Validation (`@NotBlank`, `@Min`, `@NotNull`)

---

## 📂 Estructura del Proyecto (Package by Feature)

A diferencia de las arquitecturas tradicionales por capas pesadas, este proyecto agrupa el código por **funcionalidades de negocio**, facilitando la navegación:

```text
com.mechanicsoft
├── Features
│   └── Vehiculos
│       ├── config
│       ├── controller
│       ├── entity
│       ├── repository
│       └── service
└── MechanicsoftApiApplication.java
```

---

## 🚀 Cómo empezar

### Opción A — con Docker (la más rápida)

Requisitos: Docker y Docker Compose.

```bash
docker compose up --build
```

Esto levanta PostgreSQL y la API juntos, ya conectados entre sí. Cuando termine de arrancar, la API queda lista en **http://localhost:9769**.

Para detenerlo: `docker compose down` (agrega `-v` si además quieres borrar los datos de la base de datos).

### Opción B — local, sin Docker

Requisitos:

*   JDK 17 o superior.
*   PostgreSQL corriendo localmente (puerto 5432).

Pasos:

1.  Crea una base de datos llamada `meso` en tu PostgreSQL local.
2.  Si tu usuario/contraseña de Postgres no son `postgres` / `12345678`, ajústalos en `src/main/resources/application-Dev.properties` (el perfil `Dev` ya está activo por defecto, así que no hace falta pasarlo por parámetro).
3.  Ejecuta desde la raíz del proyecto:

    ```bash
    ./mvnw spring-boot:run
    ```

Cuando veas el banner de Spring en la consola, la API estará lista en **http://localhost:9769**.

### Endpoints disponibles

| Método | Ruta                        | Descripción                  |
|--------|-----------------------------|-------------------------------|
| GET    | `/api/vehiculos`            | Lista todos los vehículos     |
| GET    | `/api/vehiculos/{id}`       | Busca un vehículo por id      |
| GET    | `/api/vehiculos/placa/{placa}` | Busca un vehículo por placa |
| POST   | `/api/vehiculos`            | Registra un vehículo nuevo    |
| PUT    | `/api/vehiculos/{id}`       | Actualiza un vehículo existente |
| DELETE | `/api/vehiculos/{id}`       | Elimina un vehículo           |

Este backend sirve principalmente al frontend **MechanicSoftIU** (React + Vite), que por defecto espera encontrarlo en `http://localhost:9769`.

---

## 👥 Creadores & Equipo de Desarrollo
Este proyecto es diseñado, desarrollado y mantenido con ❤️ por:

Cristian Camacho — GitHub Profile
Thomas Prado
Andres Varon

Desarrollado como parte de una solución de software profesional para la gestión automotriz.
