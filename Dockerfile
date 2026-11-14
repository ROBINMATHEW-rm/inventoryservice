
# =====================================================
# STAGE 1: BUILD THE SPRING BOOT APPLICATION
# =====================================================

# Use Maven with Java 17 to compile and package the application.
# AS builder gives this build stage the name "builder".
FROM maven:3.9.9-amazoncorretto-17 AS builder

# Set the working directory inside the build container.
WORKDIR /build

# Copy pom.xml first so Docker can cache the dependency layer.
# Dependencies don't need to be downloaded again if only source code changes.
COPY pom.xml .

# Download Maven dependencies before copying source code.
# -B enables batch mode (non-interactive execution).
RUN mvn dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=5

# Copy application source code into the build container.
COPY src ./src

# Clean previous build output and package the Spring Boot application.
# -DskipTests skips test execution; run tests in CI/CD separately.
RUN mvn clean package -DskipTests -B


# =====================================================
# STAGE 2: CREATE THE PRODUCTION RUNTIME IMAGE
# =====================================================

# Start a fresh image with Java 17 runtime.
# Maven and the source code from the builder stage are not included.
FROM amazoncorretto:17-alpine

# Set the working directory inside the runtime container.
WORKDIR /app

# Install wget, which will be used by the Docker health check.
# --no-cache avoids retaining the Alpine package index cache.
RUN apk add --no-cache wget

# Create a dedicated Linux group and non-root user.
# The application will run with limited privileges for better security.
RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup

# Copy the generated JAR from the builder stage into this runtime image.
# --from=builder selects the previous build stage.
# --chown assigns ownership to the non-root application user.
COPY --from=builder --chown=appuser:appgroup \
    /build/target/inventoryservice-0.0.1-SNAPSHOT.jar app.jar

# Run the application as appuser instead of root.
USER appuser

# Define an environment variable for configurable JVM options.
# These can be supplied when starting the container.
ENV JAVA_OPTS=""

# Document that the Spring Boot application listens on port 8080.
# EXPOSE does not publish the port to the host machine.
EXPOSE 8091

# Configure Docker to periodically check application health.
# interval: Run the health check every 30 seconds.
# timeout: Allow 10 seconds for each check.
# start-period: Allow 60 seconds for application startup.
# retries: Mark unhealthy after 3 consecutive failures.
# CMD: Execute wget against the Spring Boot Actuator health endpoint.
HEALTHCHECK --interval=30s \
    --timeout=10s \
    --start-period=60s \
    --retries=3 \
    CMD wget -q -O /dev/null \
    http://localhost:8080/actuator/health || exit 1

# Start the Spring Boot application when the container starts.
# sh -c expands the JAVA_OPTS environment variable.
# exec replaces the shell process with Java for proper signal handling.
# -jar app.jar executes the packaged Spring Boot application.
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:-} -jar app.jar"]
