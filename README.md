# 🛒 RevShop P2 — Comprehensive E-Commerce Platform

RevShop P2 is a full-stack, enterprise-level B2C e-commerce platform built as a Phase 2 Capstone Project. It brings together sellers who wish to manage their inventory and buyers looking for an intuitive shopping experience.

The application follows modularity, Separation of Concerns (SoC), and the DRY principle while employing modern development practices like Token-Based Authentication, Aspect-Oriented Programming (AOP) for logging, and comprehensive Unit Testing.

---

## 🛠️ Tech Stack & Architecture

### Backend (Java / Spring Boot)
- **Framework:** Spring Boot 3.x with Spring Web, Spring Data JPA, and Spring Security
- **Database:** MySQL (Production) & H2 (In-memory for Testing)
- **Security:** JWT (JSON Web Tokens) for authentication, BCrypt for password hashing
- **Logging:** Log4j2 configured with Aspect-Oriented Programming (AOP) for clean controller/service/repository logging
- **Testing:** JUnit 5, Mockito (Achieving 100% passing tests)
- **Build tool:** Maven

### Frontend (TypeScript / Angular)
- **Framework:** Angular 16
- **Styling:** CSS3, Flexbox/Grid
- **Routing:** Angular Router (Guards enabled)

---

## ✨ Key Features

### 👤 Authentication & Authorization
- Separate roles: `BUYER` and `SELLER`.
- Secure JWT-based Login/Registration.
- Automatic routing guards for role-based views.
- "Forgot Password" / Resets.

### 🏪 Seller Dashboard (Inventory Management)
- **Product Management:** Add, edit, soft-delete, and view products.
- **Stock Thresholds:** Low-stock notifications and custom thresholds per product.
- **Pricing:** Validations ensuring Selling Price never exceeds MRP.

### 🛍️ Buyer Experience
- **Search & Filter:** Find products by name, keyword, or category.
- **Cart Management:** Add to cart, view subtotal, edit quantities, and remove items (calculating real-time stock).
- **Favorites / Wishlist:** Toggle products as favorites for later viewing.
- **Checkout & Orders:** Multi-step checkout, dummy payment integration (COD, Credit Card).
- **Reviews:** Rating (1-5) and comments *only* on purchased products.

---

## ⚙️ Prerequisites for Local Setup

Before you start, ensure you have the following software installed:
1. **Java Development Kit (JDK) 17+**
2. **Node.js (v18+)** and **npm**
3. **Angular CLI (v16+)**: Install globally via `npm install -g @angular/cli@16`
4. **MySQL Database Server (v8+)**
5. **Git**
6. IDEs: **IntelliJ IDEA / Eclipse** (for backend) and **VS Code** (for frontend)

---

## 🚀 Setup & Installation Instructions

Follow these step-by-step instructions to get the application running on your local machine.

### Phase 1: Database Setup (MySQL)

1. Open your MySQL client (MySQL Workbench or terminal).
2. Create the database for the project:
   ```sql
   CREATE DATABASE revshop_p2;
   ```
3. Update the credentials in the Spring Boot application configuration.
   Navigate to `backend/src/main/resources/application.properties` and verify/update:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/revshop_p2
   spring.datasource.username=root
   spring.datasource.password=root      # Change this to your MySQL password
   
   # Note: The database will be automatically seeded with dummy data on startup 
   # because of ddl-auto=create and the DataSeeder.java component.
   ```

### Phase 2: Backend Setup (Spring Boot)

1. Open a terminal and navigate to the `backend` directory:
   ```bash
   cd RevShop_P2/backend
   ```
2. Clean and install dependencies using Maven:
   ```bash
   mvn clean install
   ```
3. Run the Spring Boot Application:
   ```bash
   mvn spring-boot:run
   ```
   *Alternative: Open the `backend` folder in IntelliJ IDEA and run `BackendApplication.java`.*
4. The server will start on **`http://localhost:8080`**.
5. Check the logs. You will see AOP Logger initializing and the `DataSeeder` injecting test users and categories.

### Phase 3: Frontend Setup (Angular)

1. Open a **new** terminal and navigate to the `frontend` directory:
   ```bash
   cd RevShop_P2/frontend
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Start the Angular development server:
   ```bash
   ng serve --open
   ```
4. The application will compile and automatically open in your default browser at **`http://localhost:4200`**.

---

## 🧪 Testing

The application is thoroughly unit-tested using **JUnit 5** and **Mockito**.
- An in-memory **H2 database** is utilized during the testing phase (configured in `log4j2-test.xml` and `test/resources/application.properties`) to prevent interference with the local MySQL DB.
- **69 Unit Tests** cover all primary services including Authentication, Products, Orders, Cart, Reviews, and Favorites.

To execute tests and view the results:
```bash
cd backend
mvn test
```

---

## 📜 Logging

Centralized **Aspect-Oriented Logging** is implemented via Spring AOP (`LoggingAspect.java`) and **Log4j2**:
- Logs are intercepted at the Service, Controller, and Repository layers without polluting business logic.
- Console output is color-coded.
- File logs are saved in the `backend/logs` directory.
- A rolling file appender generates daily logs and caps log files at `10MB`.

---

## 👤 Default Users (from DataSeeder)

If you have `ddl-auto=create` enabled, the following users are automatically seeded for testing:

| Role   | Email                  | Password |
|--------|------------------------|----------|
| SELLER | seller@revature.com    | password |
| BUYER  | buyer@revature.com     | password |

---

## 📞 Key API Endpoints overview

| HTTP | Endpoint | Description | Layer |
|------|----------|-------------|-------|
| POST | `/api/auth/register` | Register new user/seller | Public |
| POST | `/api/auth/login` | Login and obtain JWT Token | Public |
| GET  | `/api/products` | Get products (paginated) | Public |
| POST | `/api/seller/products` | Add a new product | Seller only |
| GET  | `/api/cart/{userId}` | Get user cart | Buyer only |
| POST | `/api/orders/checkout` | Checkout cart / Place Order | Buyer only |

*(Note: API is generally protected via `Bearer` JWT tokens in the `Authorization` header)*

---

### Folder Structure Overview

```text
RevShop_P2/
├── backend/                     # Spring Boot Application
│   ├── src/main/java...         # Java Source files
│   │   ├── aspect/              # AOP Logging
│   │   ├── config/              # Security config, Seeder
│   │   ├── controller/          # REST Endpoints
│   │   ├── dto/                 # Request/Response objects, Error Handling
│   │   ├── exception/           # GlobalExceptionHandlers
│   │   ├── model/               # JPA Entities
│   │   ├── repository/          # Data Access (JPA)
│   │   └── service/             # Business Logic & Validation
│   └── src/test...              # JUnit 5 & Mockito test suite
│
└── frontend/                    # Angular Application
    ├── src/app/
    │   ├── components/          # Reusable UI parts & pages
    │   ├── services/            # API integration & auth state
    │   └── guards/              # Role-based route protection
```