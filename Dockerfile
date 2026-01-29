# ==========================================
# Stage 1: Build Layer
# Uses Maven and OpenJDK 21 to compile the code
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Cached dependency layer
COPY pom.xml .
RUN mvn dependency:go-offline

# Source code layer
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Runtime Layer
# Minimal JRE image for production readiness
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security (Simulating multi-tenant cloud environment)
RUN addgroup -S ece465 && adduser -S student -G ece465
USER student

# Copy the Uber-JAR from the builder stage
COPY --from=builder /app/target/distrComputingJourney-2.0.0-SNAPSHOT.jar app.jar
COPY --from=builder /app/target/original-distrComputingJourney-2.0.0-SNAPSHOT.jar original-app.jar

# Expose the server port
EXPOSE 1971

# Default entrypoint (can be overridden)
ENTRYPOINT ["java", "-jar", "app.jar"]
