# ==============================================================================
# CareerShield AI - Multi-Stage Production Dockerfile
# Combines Java Spring Boot 3.2.3 Backend & Python FastAPI NLP Microservice
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build the Java Spring Boot Application
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

# Copy Maven files first for optimal layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x ./mvnw

# Resolve and download Maven dependencies
RUN ./mvnw dependency:go-offline -B

# Copy project source code and build executable JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ------------------------------------------------------------------------------
# Stage 2: Production Runtime (OpenJDK 17 + Python 3.10)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install Python 3, pip, venv, and curl
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    python3-pip \
    python3-venv \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set up Python ML microservice
COPY ml-service /app/ml-service
RUN python3 -m venv /app/venv && \
    /app/venv/bin/pip install --no-cache-dir -r /app/ml-service/requirements.txt

# Copy built Spring Boot JAR from builder
COPY --from=builder /build/target/careershield-*.jar /app/app.jar

# Create persistence directories for uploads and embedded DB
RUN mkdir -p /app/uploads/evidence /app/data

# Default production environment variables
ENV PORT=8080
ENV CAREERSHIELD_ML_SERVICE_URL=http://127.0.0.1:8000/predict
ENV CAREERSHIELD_UPLOAD_DIR=/app/uploads/evidence
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/careershield_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDER=HIGH
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

# Copy and configure entrypoint script
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Expose HTTP port
EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
