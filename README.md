# Prattlers Social Network

<div align="center">

![Java 21](https://img.shields.io/badge/Java_21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=redis&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Testcontainers](https://img.shields.io/badge/Testcontainers-1D63ED?style=for-the-badge&logo=testcontainers&logoColor=white)
![WireMock](https://img.shields.io/badge/WireMock-FF6C37?style=for-the-badge&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge&logo=websocket&logoColor=white)

**Prattlers** — микросервисная система социальной платформы, вдохновленная функционалом популярных социальных сетей.

</div>

## **Основные технологии:**
- **Java 21**
- **Gradle**
- **Spring Framework:**
  - Spring Boot
  - Spring Web
  - Spring Security
  - Spring Data JPA
  - Spring WebSocket
  - Spring for Apache Kafka
  - Spring Cache
- **PostgreSQL**
- **MongoDB**
-  **Redis**
- **Apache Kafka**
- **JUnit**
- **Testcontainers**
- **Wiremock**
- **Docker**

## Архитектура

Система разделена на микросервисы, которые общаются между собой с помощью **REST API**:

*   **Core** — основной сервис для работы с бизнес-логикой.
*   **Subscription Service** — сервис управления подписок пользователей.

## Структура проекта

```
prattlers/
├── core/                           # Основной сервис с бизнес-логикой платформы
├── subscription-servoice/          # Сервис управления подписок пользователей
├── docker/docker-compose.yaml      # Конфигурация контейнеров
└── README.md
```
