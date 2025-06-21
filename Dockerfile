# Stage 1: Build the application JAR
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper files and the build.gradle files for caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy build.gradle files for each sub-module
COPY auth-module/build.gradle auth-module/
COPY email-otp-module/build.gradle email-otp-module/

# Copy the entire source code (after copying build files for caching optimization)
COPY . .

# Grant execute permission to the Gradle wrapper script
RUN chmod +x gradlew

# Build the Spring Boot application's fat JAR
RUN ./gradlew bootJar -x test

# --- DEBUGGING STEP: CORRECTED path for ls command ---
# Check the build/libs directory directly under /app
RUN ls -lR build/libs/ || true
# --- END DEBUGGING STEP ---


# Stage 2: Create the final lean image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory in the final image
WORKDIR /app

# --- !!! IMPORTANT CORRECTION HERE !!! ---
# The JAR is built directly into /app/build/libs/
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your Spring Boot application listens on (default is 8080)
EXPOSE 8080

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]
