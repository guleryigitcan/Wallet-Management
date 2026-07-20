Markdown
# Wallet Management System 🚀

This project is a **Full-Stack Wallet Management Application** developed by adhering to modern software architecture principles, clean code guidelines, and enterprise production standards.

While the backend is powered by a robust **Java & Spring Boot** architecture, the frontend communicates asynchronously using **React**—which shares a highly compatible declarative state and UI design philosophy with modern mobile frameworks like Jetpack Compose and SwiftUI. Additionally, the project features advanced backend integration practices such as **Redis Caching**, **RabbitMQ Message Queuing**, and **Role-Based Access Control (RBAC)**.

---

## 🛠️ Tech Stack

### Backend
* **Java 21 & Spring Boot 3.3.1**
* **Spring Security** (Basic Authentication & Role-Based Access Control)
* **Spring Data JPA** & **H2 Database** (In-memory relational database)
* **Spring Boot Starter Validation** (Declarative request validation)
* **Spring Boot Starter Data Redis** (Performance optimization & Caching)
* **Spring Boot Starter AMQP (RabbitMQ)** (Asynchronous messaging)
* **JUnit 5 & Mockito** (Unit testing)
* **Lombok** (Boilerplate code reduction)

### Frontend
* **React 18** & **Vite** (Fast, lightweight web UI)
* **ESLint** (Code standards linter)

---

## 📐 Architecture & Design Principles

The project strictly follows the industry-standard **Multi-tier (Layered) Architecture**:

1. **Security & Validation Layer:** Incoming requests are intercepted, authenticated, and verified (`@NotBlank`, `@DecimalMin`) before they ever reach the business services.
2. **DTO (Data Transfer Object) Layer:** Database entities (`Asset.java`) are completely isolated from the outer API layer for security and flexibility. Data transport is managed entirely via dedicated Request/Response DTOs.
3. **Global Exception Handling:** All application-wide errors are intercepted globally by a controller advice, returning a clean, unified JSON error template to the client.
4. **Redis Caching (Simulated):** Frequently read data (`getAllAssets`) is cached in memory to reduce database load. When a write operation occurs (`createAsset`), the cache is automatically evicted to maintain data consistency (`@Cacheable` / `@CacheEvict`).
5. **RabbitMQ Asynchronous Messaging:** Once an asset is successfully persisted, a message is dispatched to the message queue via `RabbitTemplate` to trigger background processes (such as mock notifications) without blocking the main thread.

---

## 🔒 Security & Role-Based Access Control (RBAC)

The application configures two distinct user roles:

| Username | Password | Role | Permissions |
| :--- |:---------| :--- | :--- |
| **user** | user123  | `USER` | Read assets (GET), Create assets (POST) |
| **admin** | passowrd | `ADMIN` | Read assets (GET), Create assets (POST), Delete assets (DELETE) |

*If an unauthenticated client or an unauthorized role (`USER`) attempts a delete operation, the security filter chain immediately rejects the request with an HTTP `403 Forbidden` status, which is gracefully handled and displayed on the UI.*

---

## 🚀 Running the Project Locally

### 1. Run Backend (Spring Boot)
1. Open the project in your favorite IDE (IntelliJ IDEA, Eclipse, etc.).
2. Verify that `spring.cache.type=simple` is active in `src/main/resources/application.properties` (this allows simulated caching in local memory without requiring a running Redis instance).
3. Run the main class `WalletManagementApplication.java`.
4. The API will start on **`http://localhost:8080`**.
5. To inspect the database tables in real-time, navigate to **`http://localhost:8080/h2-console`** in your browser.

### 2. Run Frontend (React)
1. Navigate to the `wallet-frontend` directory in your terminal:
   ```bash
   cd wallet-frontend

Install the node packages:

Bash
npm install
Start the development server:

Bash
npm run dev
Open http://localhost:5173 in your browser to interact with the application.