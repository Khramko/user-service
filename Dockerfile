FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY target/health-service-0.0.2-SNAPSHOT.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]