# Interactive Adventure Book Engine

An interactive text-based adventure game platform built with a decoupled **Spring Boot** backend and an **Angular** frontend. The application features multi-user session isolation via anonymous client UUID headers, persistent game loops with health tracking, and dynamic book imports.

---

## 🚀 Key Features

* **Decoupled Architecture:**
    * **Book Management:** Handles global catalog operations (`getAllBooks`, `importBook`) independently.
    * **Game Engine Service:** Dedicated exclusively to game loop mechanics (`startGame`, `makeChoice`, `saveGame`, `resumeGame`) and consequence calculations.
* **Anonymous Multi-User Session Persistence:**
    * Automatically generates and caches a globally unique client UUID via `crypto.randomUUID()` in the browser's `localStorage`.
    * Passes the identifier securely through an `X-User-Id` HTTP header.
    * Allows players to close or restart their browsers without losing progress, while isolating concurrent players cleanly on the server side.
* **Dynamic Consequences:** Supports health tracking (`LOSE_HEALTH`, etc.) and branch validation to ensure seamless storytelling loops.

---

## 🛠️ Tech Stack

* **Backend:** Java 17+, Spring Boot, Spring Data JPA / Hibernate, PostgreSQL / MySQL
* **Frontend:** Angular, TypeScript, RxJS, Modern CSS
* **Infrastructure:** Docker & Docker Compose (Database containerization)

---

## ⚙️ Getting Started & Prerequisites

### 1. Backend Setup (Spring Boot)
1. Clone the repository and navigate to the backend directory.
2. Ensure your database container (PostgreSQL or MySQL) is running via Docker.
3. Configure your database and Hibernate settings in `application.yml` (or `application.properties`):
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/your_database
       username: your_username
       password: your_password
     jpa:
       hibernate:
         ddl-auto: create # Use 'update' once your schema is fully established
       show-sql: true