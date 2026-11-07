# ==============================================================================
# Stage 1: Build the Spring Boot application using Maven Wrapper and Java 21 JDK
# ==============================================================================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

# Copy Maven Wrapper files and pom.xml first to take advantage of layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Ensure Unix line endings and execution permissions on mvnw
RUN chmod +x mvnw

# Pre-fetch project dependencies to speed up subsequent builds
RUN ./mvnw dependency:go-offline -B || true

# Copy application source code
COPY src ./src

# Build production executable JAR (skipping unit tests during Docker build)
RUN ./mvnw clean package -DskipTests -B

# ==============================================================================
# Stage 2: Production Runtime using lightweight Java 21 JRE
# ==============================================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Render assigns a dynamic port via the PORT environment variable (default: 8080)
ENV PORT=8080
EXPOSE ${PORT}

# JVM tuning for containerized memory limits
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Create a dedicated non-root user and directory for file uploads
RUN groupadd -r spring && useradd -r -g spring spring \
    && mkdir -p /app/uploads/items \
    && chown -R spring:spring /app

# Copy the generated JAR file from the builder stage
COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

# Run the container under the non-root user for security
USER spring:spring

# Start the Spring Boot application with PORT binding
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT}"]
