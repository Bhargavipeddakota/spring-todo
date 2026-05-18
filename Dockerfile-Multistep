# -------- Build Stage --------
FROM gradle:9.5.0-jdk25 AS builder

WORKDIR /app

# Copy Gradle files first for better layer caching
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build application
RUN ./gradlew clean bootJar --no-daemon

# -------- Runtime Stage --------
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy only the built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]