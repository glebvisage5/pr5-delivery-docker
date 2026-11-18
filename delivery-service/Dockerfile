# ---------- этап 1: сборка jar ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- этап 2: образ для запуска ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# wget (busybox) уже есть в образе и используется в healthcheck
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /build/target/app.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
