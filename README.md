# NIRMAN LEDGER

**Construction Contractor Management System** — Manage labour attendance, weekly payroll, expenses, and owner reporting.

---

## Tech Stack

### Backend
- Java 21 + Spring Boot 3.2
- Spring Security + JWT Authentication
- Spring Data JPA + PostgreSQL
- OpenPDF (PDF Export)
- Firebase Admin SDK (optional storage)
- Swagger / SpringDoc OpenAPI
- Maven + Lombok + Docker

### Mobile
- Native Android (Java)
- MVVM Architecture
- Retrofit 2 + OkHttp3
- Material Design Components
- Glide (image loading)

---

## Project Structure

```
NIRMAN LEDGER/
├── backend/                    # Spring Boot API Server
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/nirman/ledger/
│       ├── NirmanLedgerApplication.java
│       ├── config/             # Security, JWT, Firebase, OpenAPI
│       ├── controller/         # REST Controllers
│       ├── dto/                # Request/Response DTOs
│       ├── exception/          # Global Exception Handler
│       ├── model/              # JPA Entities & Enums
│       ├── repository/         # JPA Repositories
│       └── service/            # Business Logic
├── android/                    # Android Mobile App
│   ├── app/
│   │   ├── build.gradle
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/nirman/ledger/
│   │       │   ├── api/        # Retrofit Client & Service
│   │       │   ├── model/      # Data Models
│   │       │   ├── ui/         # Activities & Fragments
│   │       │   └── util/       # SessionManager
│   │       └── res/            # Layouts, Colors, Themes, Menus
│   ├── build.gradle
│   └── settings.gradle
├── database/
│   └── schema.sql              # PostgreSQL DDL + Seed Data
├── docker-compose.yml          # Docker Compose orchestration
└── README.md
```

---

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 15+ (or Docker)
- Android Studio (Hedgehog+)

### 1. Database Setup

**Option A: Docker (recommended)**
```bash
docker-compose up -d db
```
This creates a `nirman_ledger` database and auto-runs `schema.sql`.

**Option B: Manual**
```bash
createdb nirman_ledger
psql -d nirman_ledger -f database/schema.sql
```

### 2. Backend Setup

```bash
cd backend

# Build
mvn clean package -DskipTests

# Run
java -jar target/ledger-1.0.0.jar
```

Or with Docker:
```bash
docker-compose up --build
```

The API will be available at: **http://localhost:8080**

### 3. Swagger API Documentation

Open: **http://localhost:8080/swagger-ui.html**

All endpoints are documented with request/response schemas. Use the "Authorize" button with your JWT token.

### 4. Android App Setup

1. Open the `android/` folder in **Android Studio**
2. In `RetrofitClient.java`, update `BASE_URL`:
   - Emulator: `http://10.0.2.2:8080/`
   - Physical device: Use your machine's local IP (e.g., `http://192.168.1.x:8080/`)
3. Build & Run on emulator or device

### 5. APK Generation

```bash
cd android
./gradlew assembleDebug
```
APK location: `android/app/build/outputs/apk/debug/app-debug.apk`

For release:
```bash
./gradlew assembleRelease
```

---

## API Endpoints

| Module      | Method | Endpoint             | Auth     |
|------------|--------|---------------------|----------|
| Auth       | POST   | `/auth/register`    | Public   |
| Auth       | POST   | `/auth/login`       | Public   |
| Sites      | GET    | `/sites`            | Both     |
| Sites      | POST   | `/sites`            | Contractor |
| Sites      | PUT    | `/sites/{id}`       | Contractor |
| Sites      | DELETE | `/sites/{id}`       | Contractor |
| Workers    | GET    | `/workers?siteId=`  | Both     |
| Workers    | POST   | `/workers`          | Contractor |
| Workers    | PUT    | `/workers/{id}`     | Contractor |
| Workers    | DELETE | `/workers/{id}`     | Contractor |
| Attendance | GET    | `/attendance`       | Both     |
| Attendance | POST   | `/attendance`       | Contractor |
| Advance    | GET    | `/advance?siteId=`  | Both     |
| Advance    | POST   | `/advance`          | Contractor |
| Payroll    | GET    | `/payroll/weekly`   | Both     |
| Payroll    | POST   | `/payroll/pay?id=`  | Contractor |
| Expenses   | GET    | `/expenses?siteId=` | Both     |
| Expenses   | POST   | `/expenses`         | Contractor |
| Dashboard  | GET    | `/dashboard?siteId=`| Both     |
| Reports    | GET    | `/reports/daily`    | Both     |
| Reports    | GET    | `/reports/weekly`   | Both     |
| Reports    | GET    | `/reports/monthly`  | Both     |

---

## Roles

| Role       | Permissions                              |
|-----------|------------------------------------------|
| CONTRACTOR | Full CRUD on all modules                |
| OWNER      | View-only (GET) across assigned sites   |

---

## Weekly Payroll Calculation

```
Cycle: SUNDAY → SATURDAY

finalAmount = (fullDays × dailyWage) + (halfDays × dailyWage / 2) − totalAdvances
```

Payroll records are auto-generated when the weekly endpoint is called.

---

## Environment Variables

| Variable                  | Default                         | Description               |
|--------------------------|--------------------------------|---------------------------|
| SPRING_DATASOURCE_URL    | jdbc:postgresql://localhost:5432/nirman_ledger | DB URL |
| SPRING_DATASOURCE_USERNAME | postgres                     | DB Username               |
| SPRING_DATASOURCE_PASSWORD | postgres                     | DB Password               |
| JWT_SECRET               | (hex string)                   | HMAC SHA-256 signing key  |
| JWT_EXPIRATION           | 86400000                       | Token TTL in milliseconds |
| STORAGE_TYPE             | LOCAL                          | LOCAL or FIREBASE         |
| FIREBASE_BUCKET          | (empty)                        | Firebase Storage bucket   |
| FIREBASE_CONFIG_PATH     | (empty)                        | Path to service account   |

---

## Docker

```bash
# Start all services
docker-compose up --build -d

# Stop
docker-compose down

# View logs
docker-compose logs -f backend
```

---

## Seed Data Credentials

| Username     | Password  | Role       |
|-------------|-----------|------------|
| contractor1 | password  | CONTRACTOR |
| owner1      | password  | OWNER      |

---

## License

This project is proprietary software. All rights reserved.
