FROM amazoncorretto:21-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем jar файл
COPY target/health-service-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт
EXPOSE 8000

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]