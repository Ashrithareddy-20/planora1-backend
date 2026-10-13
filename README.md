# Planora – Backend API Service

Spring Boot REST API service and AI-powered RAG Engine for the **Planora Event Planning & Vendor Recommendation System**.

---

## 🏗️ Tech Stack

- **Framework:** Java 17+, Spring Boot 3.3.5, Spring MVC
- **Security:** Spring Security 6, JWT (JSON Web Tokens), BCrypt Password Hashing
- **Persistence:** Spring Data JPA, Hibernate, MySQL 8 (H2 in-memory fallback)
- **AI & RAG:** Spring WebClient (OpenAI / Gemini / Ollama compatible) + Built-in Local RAG NLP Engine
- **Documentation:** SpringDoc OpenAPI 3 (Swagger UI)
- **Monitoring & Caching:** Spring Boot Actuator, Spring Cache
- **Build Tool:** Apache Maven
- **Containerization:** Docker (Multi-stage build)

---

## 🚀 Quick Start (Local)

### Pre-requisites:
- Java 17+ (JDK 17, 21, or 24)
- Maven 3.8+
- MySQL 8 (Optional - defaults to in-memory H2 if MySQL is not running)

### Running locally:
```bash
# In backend root
mvn clean spring-boot:run
```

- **API Base:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **H2 Console:** `http://localhost:8080/h2-console` (JDBC: `jdbc:h2:mem:planora_db`, user: `sa`, no password)

---

## 🗄️ Database Setup (MySQL)

A complete SQL database initialization script is included at [`database.sql`](./database.sql) (or `src/main/resources/schema.sql` and `data.sql`).

To load into your MySQL database:
```bash
mysql -u root -p < database.sql
```

To run the backend with MySQL profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 🔑 Pre-Seeded Demo Accounts

| Role | Email | Password |
| :--- | :--- | :--- |
| **Client / Host** | `user@planora.com` | `password123` |
| **Vendor** | `vendor@planora.com` | `password123` |
| **Admin** | `admin@planora.com` | `password123` |

---

## 🚂 Deploy to Railway

1. Go to [railway.app](https://railway.app) -> **"New Project"**.
2. Click **"Provision MySQL"**.
   - Open the MySQL query editor and paste the contents of `database.sql` to initialize tables and seed data.
3. Click **"+ New"** -> **"GitHub Repo"** -> select this backend repository.
4. Set Environment Variables:
   - `SPRING_PROFILES_ACTIVE`: `mysql`
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `SPRING_DATASOURCE_USERNAME`: `${{MySQL.MYSQLUSER}}`
   - `SPRING_DATASOURCE_PASSWORD`: `${{MySQL.MYSQLPASSWORD}}`
   - `AI_API_KEY`: *(Optional OpenAI / Gemini / OpenRouter API Key)*
5. Go to **Settings** -> **Networking** -> click **"Generate Domain"** to get your public backend URL.
