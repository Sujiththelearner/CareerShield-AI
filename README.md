# 🛡️ CareerShield AI
### *AI-Based Fake Internship & Job Posting Detection System*
> **Tagline:** *"Verify Before You Apply."*

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy)
[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📌 Project Overview
**CareerShield AI** is an enterprise-grade Java web application engineered to protect collegiate students, engineering scholars, and job applicants from fraudulent internships, employment advance-fee extortion, corporate impersonation, and identity harvesting scams.

Unlike primitive keyword scanners that output an arbitrary percentage without context, CareerShield AI generates an **Explainable Safety Report** combining:
1. **Multi-Factor Forensic Rule Engine** (Gang of Four Strategy Pattern in pure Java)
2. **Machine Learning NLP Scam Classifier** (TF-IDF & Logistic Regression sidecar microservice)
3. **Resilient Java Fallback Engine** (Internal lexical heuristics for 100% uptime)
4. **Corporate Domain Authentication** (Detects MNC impersonation using free public webmail)
5. **Digital Evidence Vault** (Secure forensic repository for student incident reporting)
6. **Administrator Threat Telemetry** (Campus-wide scam metrics and audit trails)

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend Core** | Java 17 LTS, Spring Boot 3.2.3, Spring MVC, REST APIs |
| **Persistence / ORM** | Spring Data JPA, Hibernate ORM, JPQL |
| **Database** | MySQL 8.0 / Embedded H2 Persistent Mode (Zero-Config Fallback) |
| **Security & Hashing** | Spring Security 6 with BCrypt Password Salting & Hashing |
| **Frontend GUI** | HTML5, CSS3 Custom Cyber SaaS Theme, Bootstrap 5.3, Chart.js |
| **AI / NLP Microservice** | Python FastAPI / Scikit-Learn (TF-IDF + Logistic Regression) |
| **Build & Management** | Apache Maven 3.9.6 + Maven Wrapper (`./mvnw`) |
| **Containerization** | Multi-Stage Dockerfile (OpenJDK 17 + Python 3.10) |

---

## 🏛️ Java Architecture & OOP Concepts Demonstrated

This project strictly demonstrates core Java and enterprise design patterns:
- **Strategy Pattern & Polymorphism**: Detection rules implement the `RiskFactorRule` interface (`DomainSpoofingRule`, `PaymentDemandRule`, `SuspiciousPlatformRule`, `SalaryAnomalyRule`, `UrgencyLanguageRule`). Spring automatically discovers and executes `List<RiskFactorRule>`.
- **Abstraction & Decoupling**: Pure Java interfaces decouple web controllers (`JobScanController`, `AuthController`, `EvidenceController`, `AdminController`) from business logic services.
- **Inheritance**: All JPA entities inherit common auditing fields (`id`, `createdAt`, `updatedAt`) and lifecycle callbacks (`@PrePersist`, `@PreUpdate`) from `BaseEntity`.
- **Encapsulation**: Private class fields accessed exclusively through strongly typed accessors. Sensitive fields like `passwordHash` are protected with `@JsonIgnore`.
- **Collections & Streams API**: Penalty calculations, factor filtering, and dashboard telemetry are computed using declarative Java Streams.
- **Resilient Fallback Design**: If the Python ML microservice is offline or latent, `MLPredictionService` automatically executes an internal Java NLP lexical engine, guaranteeing 100% operational availability.

---

## 🚀 Live Cloud Deployment

### Option A: One-Click Deploy to Render
1. Click the **Deploy to Render** button or connect this GitHub repository on [Render.com](https://render.com).
2. Render detects `render.yaml` or `Dockerfile` and builds the service automatically.
3. Your live application will be instantly accessible at: `https://<your-app-name>.onrender.com`

### Option B: Deploy with Docker
```bash
# Build the unified production container
docker build -t careershield-ai .

# Run the container (defaults to port 8080)
docker run -p 8080:8080 careershield-ai
```

### Option C: Railway / Koyeb / Fly.io
Deploy this repository directly as a Docker Web Service on Railway, Koyeb, or Fly.io. The multi-stage `Dockerfile` packages both the Java backend and the Python ML microservice together.

---

## ⚙️ Environment Variables Reference

All credentials and ports are configurable via environment variables in production:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `PORT` | HTTP server port | `8080` |
| `SPRING_DATASOURCE_URL` | JDBC database connection URL | Embedded persistent storage |
| `SPRING_DATASOURCE_USERNAME` | Database username | `sa` / `root` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | *(Empty / Secret)* |
| `CAREERSHIELD_ML_SERVICE_URL` | Python ML endpoint | `http://127.0.0.1:8000/predict` |
| `CAREERSHIELD_UPLOAD_DIR` | Evidence upload storage path | `uploads/evidence` |

---

## 💻 Local Development Setup

### Prerequisites
- JDK 17+
- Maven 3.8+ (or use included `./mvnw`)
- MySQL 8.0 (optional; embedded persistent DB activates if MySQL is absent)

### Running Locally
```bash
# Windows
.\run.ps1
# or
mvn spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Open browser at: **http://localhost:8080**

---

## 🔑 Demo Credentials

- **Administrator:** `admin@careershield.ai` / `Admin@12345`
- **Student Demo:** `sujith@student.ac.in` / `Password@123`
- **Or register a new account** directly at `/register.html`

---

## 📄 License
This project is licensed under the MIT License.
