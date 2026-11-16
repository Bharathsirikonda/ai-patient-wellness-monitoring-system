# Multi-stage Dockerfile for AI-Enabled Patient Wellness Monitoring System

# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy Maven descriptor and download dependencies
COPY pom.xml .
COPY src ./src

# Install maven
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

# Stage 2: Runtime stage
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

# Environment defaults
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Healthcheck definition
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
