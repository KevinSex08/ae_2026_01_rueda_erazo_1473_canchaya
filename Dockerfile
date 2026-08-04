FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy gradle wrappers and build files first to cache dependencies
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts ./

# Ensure gradlew is executable
RUN chmod +x ./gradlew

# Download dependencies (this layer will be cached unless build files change)
RUN ./gradlew dependencies --no-daemon

# Copy source code and build
COPY src/ src/
RUN ./gradlew build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
