FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем jar файл
COPY target/health-service-0.0.2-SNAPSHOT.jar app.jar

# Открываем порт
EXPOSE 8000

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]