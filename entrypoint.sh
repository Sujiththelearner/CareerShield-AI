#!/bin/bash
set -e

echo "========================================================================"
echo "          CareerShield AI - Production Server Initialization           "
echo "========================================================================"

# 1. Start Python FastAPI ML Microservice in background
echo "🚀 Starting CareerShield AI Python ML Service on http://127.0.0.1:8000..."
/app/venv/bin/uvicorn app:app --app-dir /app/ml-service --host 127.0.0.1 --port 8000 &

# 2. Wait up to 10 seconds for ML service to become healthy
echo "⏳ Awaiting Python ML Service health check..."
for i in {1..10}; do
  if curl -s http://127.0.0.1:8000/health > /dev/null 2>&1; then
    echo "✅ Python ML Service is ONLINE and HEALTHY."
    break
  fi
  sleep 1
done

# 3. Check database configuration
if [[ "$SPRING_DATASOURCE_URL" == *"mysql"* ]]; then
  echo "📦 Database: External MySQL database configured ($SPRING_DATASOURCE_URL)"
  export SPRING_DATASOURCE_DRIVER_CLASS_NAME="com.mysql.cj.jdbc.Driver"
  export SPRING_JPA_DATABASE_PLATFORM="org.hibernate.dialect.MySQLDialect"
else
  echo "📦 Database: Using persistent embedded storage (/app/data/careershield_db)"
  export SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.h2.Driver"
  export SPRING_JPA_DATABASE_PLATFORM="org.hibernate.dialect.H2Dialect"
fi

# 4. Start Java Spring Boot Application
echo "🛡️ Starting CareerShield AI Spring Boot Server on port ${PORT:-8080}..."
exec java -Dserver.port="${PORT:-8080}" -jar /app/app.jar
