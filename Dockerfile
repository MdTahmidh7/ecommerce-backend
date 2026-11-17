# Stage 1: Build the application JAR
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Install curl and unzip
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl unzip \
    && rm -rf /var/lib/apt/lists/*

# Install Gradle 8.14 using curl instead of Gradle Wrapper
RUN curl -4 -fL --retry 3 \
    https://services.gradle.org/distributions/gradle-8.14-bin.zip \
    -o /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && rm /tmp/gradle.zip

ENV GRADLE_HOME=/opt/gradle-8.14
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

# Copy the Gradle wrapper files and build.gradle files for caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy build.gradle files for each sub-module
COPY auth-module/build.gradle auth-module/
COPY email-otp-module/build.gradle email-otp-module/

# Copy the entire source code
COPY . .

# Keep Gradle version consistent with the project wrapper
RUN gradle --version

# Build the Spring Boot application's fat JAR
RUN gradle bootJar -x test --no-daemon

# Verify generated JAR
RUN ls -lh build/libs/

# Stage 2: Create the final lean image
FROM eclipse-temurin:21-jre-jammy AS runtime

# Set the working directory in the final image
WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
