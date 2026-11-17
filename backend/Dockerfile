# Multi-stage Dockerfile for AI-Enabled Patient Wellness Monitoring System Backend

# Stage 1: Build stage with pre-installed Maven & JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal, secure runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root system user for security compliance
RUN groupadd -r wellness && useradd -r -g wellness -s /bin/false wellness

# Copy compiled executable JAR from build stage
COPY --from=build /app/target/ai-patient-wellness-monitoring-system-*.jar app.jar

# Enforce non-root ownership
RUN chown -R wellness:wellness /app
USER wellness

# Expose Spring Boot HTTP port
EXPOSE 8080

# Environment defaults (defaults to local in-memory profile for instant Render boot)
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=local \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Healthcheck definition
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
