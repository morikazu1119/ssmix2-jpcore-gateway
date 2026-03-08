FROM gradle:8.14.1-jdk21 AS builder

WORKDIR /workspace
COPY . .
RUN ./gradlew --no-daemon :app:bootJar

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /workspace/app/build/libs/app-*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

