# 🔧 MechanicSoft — REST API 🚀

¡Bienvenido al backend de **MechanicSoft**! Esta es una API REST robusta, moderna y altamente escalable desarrollada en **Spring Boot**. Su objetivo principal es optimizar y automatizar la gestión de talleres automotrices, facilitando el control de ingresos de vehículos, el historial de reparaciones y la gestión inteligente de clientes.

---

## ⚡ Características Clave

*   **Gestión Inteligente de Vehículos:** Control detallado de placas, marcas, modelos, kilometraje, cilindraje, fotos y motivos de ingreso al taller.
*   **Vinculación Automática de Clientes:** Flujo inteligente que detecta si un cliente ya existe (por su teléfono) al registrar un vehículo, actualizando sus datos o creando uno nuevo en caso de ser un cliente primerizo.
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
│   ├── Clientes
│   │   ├── controller
│   │   ├── entity
│   │   ├── repository
│   │   └── service
│   └── Vehiculos
│       ├── controller
│       ├── entity
│       ├── repository
│       └── service
└── MechanicSoftApplication.java



🚀 Cómo Empezar (Desarrollo Local)
1. Prerrequisitos
Java Development Kit (JDK) 17 o superior instalado.

PostgreSQL corriendo localmente (puerto por defecto 5432).

Tu IDE favorito (IntelliJ IDEA recomendado).

2. Configurar la Base de Datos
Crea una base de datos en tu servidor PostgreSQL llamada meso. Luego, asegúrate de tener tu archivo application-dev.properties en la carpeta src/main/resources/ con tus credenciales:

Properties
spring.datasource.url=jdbc:postgresql://localhost:5432/meso
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
3. Ejecutar la Aplicación
Puedes arrancar el proyecto desde tu IDE activando el perfil de desarrollo (dev), o usar la terminal desde la raíz del proyecto:

Bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
Cuando veas el banner de Spring en la consola, la API estará lista para recibir peticiones en http://localhost:9798.

👥 Creadores & Equipo de Desarrollo
Este proyecto es diseñado, desarrollado y mantenido con ❤️ por:

Cristian Camacho — GitHub Profile
Thomas Prado
Andres Varon

Desarrollado como parte de una solución de software profesional para la gestión automotriz.
