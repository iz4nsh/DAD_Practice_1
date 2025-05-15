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
