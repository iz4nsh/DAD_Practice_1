# CPD Management System - Distributed Application

This project is part of the subject **Desarrollo de Aplicaciones Distribuidas** (3rd year, Computer Engineering Degree). It consists of the development of a **distributed web application** for managing a data center (CPD), using modern technologies and professional practices.

## 📦 Architecture Overview

The system is composed of multiple microservices:

- `api-service`: Exposes a REST API to manage instances and disks.
- `disk-service`: Manages disk creation asynchronously.
- `instance-service`: Manages virtual machine instance creation.
- `MySQL`: Relational database for persistence.
- `RabbitMQ`: Message broker for asynchronous communication.
- `HAProxy`: Load balancer for the API service.

All services are executed as Docker containers and orchestrated with Docker Compose.

---

## ⚙️ How to Run the Project

To run the entire system:

```bash
docker-compose up --build

> ⚠️ **Ensure you have Docker and Docker Compose installed.**

Access the API (behind HAProxy) at: [http://localhost/](http://localhost/)

---

## 🧪 Postman Collection

A Postman collection file `api.postman_collection.json` is included at the root of the repository. It contains example requests to test the API functionality, including:

- Create instance
- Delete instance
- Delete unassigned disk
- List instances and disks

---

## 🧱 Database Schema

The system stores information about:

- **Disks**: with fields `id`, `size`, `type`, `status`
- **Instances**: with fields `id`, `name`, `memory`, `cores`, `ip`, `status`, and a foreign key to a disk


## 👥 Team Members and Contributions

### Student 1: **Izan**

- **Implemented**: Database config, API connection, Disk creation logic

- **Key commits**:
  - Add Disk entity and repository
  - Connect API to MySQL

- **Top modified files**:
  - `Disk.java`
  - `application.properties`

> *(Repeat for other members)*
