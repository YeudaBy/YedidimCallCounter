FROM gradle:8.7.0-jdk21 AS build

WORKDIR /home/gradle/src

# Copy only what is needed for the backend and shared modules to build
COPY shared /home/gradle/src/shared
COPY backend /home/gradle/src/backend
COPY gradle /home/gradle/src/gradle
COPY gradle.properties /home/gradle/src/
COPY build.gradle.kts /home/gradle/src/
COPY settings.gradle.kts /home/gradle/src/settings_original.gradle.kts

# Create a settings.gradle.kts that excludes the :app module to avoid Android SDK requirements
RUN echo 'pluginManagement { \n\
    repositories { \n\
        google() \n\
        mavenCentral() \n\
        gradlePluginPortal() \n\
    } \n\
} \n\
dependencyResolutionManagement { \n\
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) \n\
    repositories { \n\
        google() \n\
        mavenCentral() \n\
    } \n\
    versionCatalogs { \n\
        create("ktorLibs").from("io.ktor:ktor-version-catalog:3.5.0") \n\
    } \n\
} \n\
rootProject.name = "Calls Counter" \n\
include(":shared") \n\
include(":backend") \n\
' > settings.gradle.kts

# Build the fat jar
RUN gradle :backend:buildFatJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
EXPOSE 8080
RUN mkdir /app

# Copy the built fat jar
COPY --from=build /home/gradle/src/backend/build/libs/*-all.jar /app/backend.jar

# Define default environment variables
ENV DB_URL=jdbc:postgresql://db:5432/calls_counter
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres
ENV RATE_LIMIT_REQUESTS=60
ENV RATE_LIMIT_PERIOD_SECONDS=60

ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
