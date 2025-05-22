# 🏢 CPD Management System - Distributed Application

Proyecto para la asignatura **Desarrollo de Aplicaciones Distribuidas** (3º Ingeniería Informática).  
Desarrolla una **aplicación web distribuida** para la gestión de un centro de proceso de datos (CPD), aplicando tecnologías modernas y buenas prácticas profesionales.

---

## 📚 Índice

1. [🔎 Descripción General](#-descripción-general)
2. [🗺️ Arquitectura del Sistema](#-arquitectura-del-sistema)
3. [⚙️ Puesta en Marcha](#️-puesta-en-marcha)
4. [🌐 Endpoints de la API](#-endpoints-de-la-api)
5. [🗄️ Esquema de la Base de Datos](#️-esquema-de-la-base-de-datos)
6. [🔁 Flujo Funcional](#-flujo-funcional)
7. [🔄 Comunicación y Balanceo](#-comunicación-y-balanceo)
8. [🔧 Variables de Entorno y Configuración](#-variables-de-entorno-y-configuración)
9. [🧪 Colección Postman](#-colección-postman)
10. [👥 Equipo y Contribuciones](#-equipo-y-contribuciones)
11. [📎 Recursos y Enlaces](#-recursos-y-enlaces)

---

## 🔎 Descripción General

Este sistema permite gestionar instancias virtuales y discos de un CPD de forma distribuida, soportando concurrencia, balanceo de carga y comunicación asíncrona entre microservicios.

---

## 🗺️ Arquitectura del Sistema

El sistema está compuesto por los siguientes microservicios y componentes:

- **`apiservice1` y `apiservice2`**: Exponen la API REST para gestionar instancias y discos. Balanceados por HAProxy.
- **`disk-service`**: Gestiona la creación y el ciclo de vida de los discos de forma asíncrona.
- **`instance-service`**: Gestiona la creación y el ciclo de vida de las instancias virtuales.
- **`MySQL`**: Base de datos relacional para persistencia.
- **`RabbitMQ`**: Broker de mensajería para comunicación asíncrona entre servicios.
- **`HAProxy`**: Balanceador de carga HTTP para los servicios de API.

Todos los servicios se ejecutan en contenedores Docker y se orquestan con Docker Compose.

---

## ⚙️ Puesta en Marcha

### 1️⃣ Requisitos previos

- Docker y Docker Compose instalados
- (Opcional) Java 17+ y Maven para desarrollo local

### 2️⃣ Clonar el repositorio

```bash
git clone <url-del-repo>
cd DAD_Practice_1-1
```

### 3️⃣ Arrancar todos los servicios

```bash
docker-compose up --build
```

### 4️⃣ Acceso a la aplicación

- API disponible en: [http://localhost/](http://localhost/) (a través de HAProxy)
- Todos los endpoints están documentados abajo.

---

## 🌐 Endpoints de la API

### 📦 Instancias

- `GET /api/instances`  
  Lista todas las instancias (paginado).

- `GET /api/instances?id=1`  
  Obtiene una instancia por ID.

- `POST /api/instances`  
  Crea una nueva instancia.  
  **Body ejemplo:**
  ```json
  {
    "name": "Mi instancia",
    "memory": 2048,
    "cores": 2,
    "diskType": "SSD",
    "diskSize": 100
  }
  ```

- `DELETE /api/instances?id=1`  
  Elimina una instancia por ID.

---

### 💽 Discos

- `GET /api/disks`  
  Lista todos los discos (paginado).

- `GET /api/disks?id=1`  
  Obtiene un disco por ID.

- `POST /api/disks`  
  Solicita la creación de un disco.  
  **Body ejemplo:**
  ```json
  {
    "type": "SSD",
    "size": 100
  }
  ```

- `DELETE /api/disks?id=1`  
  Elimina un disco por ID (solo si está `UNASSIGNED`).

---

## 🗄️ Esquema de la Base de Datos

- **Disks**
  - `id` (Long, PK)
  - `size` (float)
  - `type` (String)
  - `status` (String)

- **Instances**
  - `id` (Long, PK)
  - `name` (String)
  - `memory` (int)
  - `cores` (int)
  - `ip` (String)
  - `status` (String)
  - `disk_id` (Long, FK a Disk)

---

## 🔄 Flujo Funcional
- El cliente hace un POST /instances con los datos de la instancia y el disco.
- El apiservice guarda la instancia en estado DISK_REQUESTED.
- Se envía un mensaje a disk-requests (cola de RabbitMQ).
- diskservice simula la creación del disco y envía estados por disk-statuses.
- Cuando el disco está en estado ASSIGNED, el apiservice lanza la petición a instance-requests.
- instanceservice lanza la instancia y responde con IP y estado final por instance-statuses.
- El apiservice actualiza el estado e IP de la instancia en su base de datos.

---

## 🔄 Comunicación y Balanceo

- **Balanceo de carga:**  
  HAProxy distribuye las peticiones HTTP entre `apiservice1` y `apiservice2` usando round-robin.

- **Comunicación entre microservicios:**  
  Se realiza mediante colas de RabbitMQ, permitiendo desacoplar la lógica y soportar concurrencia.

---

## 🔧 Variables de Entorno y Configuración

- Todas las conexiones entre servicios (host, puerto, credenciales) se configuran mediante variables de entorno en `docker-compose.yml`.
- Spring Boot lee estas variables automáticamente (`SPRING_DATASOURCE_URL`, `SPRING_RABBITMQ_HOST`, etc).

---

## 🧪 Colección Postman

Incluida en el repositorio:  
`api.postman_collection.json`

Permite probar fácilmente:
- Crear instancia
- Eliminar instancia
- Eliminar disco no asignado
- Listar instancias y discos

---

## 👥 Equipo y Contribuciones

### 👤 Izan

- **Implementación:** Configuración de base de datos, lógica de creación de discos, integración de API y microservicios.
- **Commits clave:**
  - Añadido entidad Disk y repositorio
  - Conexión de API con MySQL
- **Archivos principales:**
  - `Disk.java`
  - `ListenerService.java`
  - `docker-compose.yml`

> *(Agrega aquí más miembros si corresponde)*

---

## 📎 Recursos y Enlaces

- [Documentación oficial de Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación de Docker Compose](https://docs.docker.com/compose/)
- [Documentación de HAProxy](https://www.haproxy.org/)
- [Documentación de RabbitMQ](https://www.rabbitmq.com/documentation.html)

---

**Universidad DAD - Práctica 1-1**  
¡Gracias por usar este proyecto! 🚀
