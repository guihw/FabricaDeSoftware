# ── Stage 1: Build ───────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B


COPY src src
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

FROM rabbitmq:4-management

RUN rabbitmq-plugins enable --offline rabbitmq_stomp