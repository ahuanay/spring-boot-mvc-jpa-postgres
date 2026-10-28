# Spring Boot MVC JPA PostgreSQL (`spring-boot-mvc-jpa-postgres`)

API REST para la gestión de empleados (alta, consulta, actualización y baja lógica) construida con **Java 21**, **Spring
Boot 3.5.16**, **Spring MVC**, **Spring Data JPA** y **PostgreSQL**.

Diseñada con **Arquitectura Hexagonal (Ports & Adapters)**, **DDD** y **CQRS**.

---

## 🧱 Arquitectura

Se separan tres responsabilidades claras:

* **Dominio (DDD)**: el modelo `Empleado` con sus Value Objects (`Email`, `Salario`), reglas e invariantes, excepciones
  y el puerto `EmpleadoRepository`.
* **Aplicación (CQRS)**: se divide en comandos (escritura: crear, actualizar, borrar) y consultas (lectura: listar y
  obtener por ID), cada una con su propio handler y con modelos de lectura desacoplados del agregado.
* **Infraestructura (Adaptadores)**: controladores REST, DTOs, mappers, adaptadores JPA (entidades, repositorios,
  specifications), configuración y manejo global de excepciones.

```
src/main/java/com/ahuanay/empleado
├── domain/          → modelo, value objects, excepciones, puertos
├── application/     → commands + handlers, queries + handlers, read models
├── common/          → paginación, auditoría, DTOs y utilidades compartidas
└── infrastructure/  → adaptadores REST, adaptadores JPA y configuración
```

---

## 🛠️ Tecnologías

* Java 21 · Spring Boot 3.5.16 · Spring MVC · Spring Data JPA / Hibernate
* PostgreSQL 16 + Flyway (migraciones)
* Lombok · MapStruct
* OpenAPI 3 / Swagger UI
* JUnit 5 · Mockito · JaCoCo
* Docker y Docker Compose

---

## ✅ Requisitos

* **Docker Desktop** (obligatorio para probar el proyecto):
  👉 https://www.docker.com/products/docker-desktop/
* **JDK 21** (solo si ejecutas la aplicación fuera de Docker; el `mvnw` viene incluido)

Verifica la instalación:

```bash
docker --version
docker compose version
```

---

## 🚀 Cómo levantar el proyecto

### Opción 1 — Solo la base de datos en Docker (app local)

Ideal para desarrollar: la base de datos corre en Docker y la aplicación se ejecuta con el perfil `dev` (puerto
**8081**, BD en el puerto **5433**).

```bash
# 1. Levantar PostgreSQL (limpia el volumen anterior)
docker compose -f compose-dev.yaml down -v
docker compose -f compose-dev.yaml up -d

# 2. Ejecutar la aplicación (el perfil dev ya es el activo por defecto)
./mvnw spring-boot:run
```

En Windows (PowerShell / CMD):

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación aplica las migraciones de Flyway automáticamente al iniciar.

### Opción 2 — Todo empaquetado en Docker (app + base de datos)

Ideal para probar el servicio completo sin instalar nada más: compila la imagen con el `Dockerfile` y levanta la API con
el perfil `prod` en el puerto **8080**.

```bash
docker compose down -v --rmi local
docker compose up -d --build
```

Para detener todo:

```bash
docker compose down -v --rmi local
```

> La primera ejecución tarda más porque descarga dependencias de Maven y construye la imagen.

---

## 🌐 URLs de prueba

| Recurso          | Solo BD en Docker (app local)          | Todo en Docker                         |
|------------------|----------------------------------------|----------------------------------------|
| API de empleados | http://localhost:8081/api/v1/empleados | http://localhost:8080/api/v1/empleados |
| Swagger UI       | http://localhost:8081/swagger-ui.html  | http://localhost:8080/swagger-ui.html  |
| OpenAPI JSON     | http://localhost:8081/v3/api-docs      | http://localhost:8080/v3/api-docs      |

---

## 📌 Endpoints principales

Base: `/api/v1/empleados`

| Método   | Ruta    | Descripción                                                                                            |
|----------|---------|--------------------------------------------------------------------------------------------------------|
| `POST`   | `/`     | Crear empleado (devuelve el ID generado)                                                               |
| `GET`    | `/`     | Listado paginado con filtros (nombre, apellido, email, puesto, rango de salario y de fecha de ingreso) |
| `GET`    | `/{id}` | Obtener empleado por ID                                                                                |
| `PUT`    | `/{id}` | Actualizar empleado                                                                                    |
| `DELETE` | `/{id}` | Baja lógica (soft delete)                                                                              |

---

## 🧪 Ejecutar pruebas

```bash
./mvnw test
```

Las pruebas usan el perfil `test` con **H2 en memoria**, por lo que no necesitan Docker.
