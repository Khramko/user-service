# Health Service

Spring Boot микросервис для управления пользователями с интеграцией PostgreSQL, Liquibase для миграций и полным набором Kubernetes манифестов для развертывания.

## 📋 Оглавление
- [Технологии](#технологии)
- [Архитектура](#архитектура)
- [Поток данных](#поток-данных)
- [Требования](#требования)
- [Быстрый старт](#быстрый-старт)
- [API Эндпоинты](#api-эндпоинты)
## 🛠 Технологии

### Backend
- **Java 17** - язык программирования
- **Spring Boot 3.2.5** - фреймворк
- **Spring Data JPA** - ORM
- **Spring Web** - REST API
- **Spring Actuator** - мониторинг и health checks
- **Liquibase** - управление миграциями базы данных
- **PostgreSQL** - реляционная база данных
- **Hibernate** - JPA реализация
- **Maven** - сборка проекта

### Инфраструктура
- **Docker** - контейнеризация
- **Kubernetes** - оркестрация
- **Helm** - управление пакетами
- **Nginx Ingress** - маршрутизация трафика
- **Prometheus** - сбор метрик (через Micrometer)

## 🏗 Архитектура
```mermaid
graph TB
    User[("👤 Пользователь")]
    Browser["🌐 Браузер"]
    Ingress["🚪 Ingress (nginx)"]
    Service["🔀 Service (ClusterIP)"]
    Pod1["📱 Spring Boot Pod 1"]
    Pod2["📱 Spring Boot Pod 2"]
    DB["🐘 PostgreSQL"]
    Job["⚙️ Migration Job"]
    
    User --> Browser
    Browser --> Ingress
    Ingress --> Service
    Service --> Pod1
    Service --> Pod2
    Pod1 --> DB
    Pod2 --> DB
    Job --> DB
```

## Поток данных
```mermaid
sequenceDiagram
    autonumber
    participant User as 👤 Пользователь
    participant Browser as 🌐 Браузер
    participant Ingress as 🚪 Ingress (nginx)
    participant Service as 🔀 Service (ClusterIP)
    participant Pod as 📱 Spring Boot Pod
    participant DB as 🐘 PostgreSQL
    participant Job as ⚙️ Migration Job
    participant Config as 📄 ConfigMap
    participant Secret as 🔐 Secret

    Note over Job,DB: Этап 1: Инициализация
    Job->>DB: Liquibase миграция
    DB-->>Job: ✅ Таблицы созданы
    
    Note over Pod,Secret: Этап 2: Загрузка конфигурации
    Pod->>Config: Чтение настроек (DB_HOST, DB_PORT)
    Pod->>Secret: Чтение секретов (DB_USER, DB_PASSWORD)
    Config-->>Pod: ✅ Конфигурация загружена
    Secret-->>Pod: ✅ Секреты получены
    
    Note over Pod,DB: Этап 3: Подключение к БД
    Pod->>DB: JDBC соединение
    DB-->>Pod: ✅ Подключение установлено
    
    Note over User,Browser: Этап 4: Обработка HTTP запросов
    User->>Browser: Ввод URL
    Browser->>Ingress: GET http://arch.homework/health
    Ingress->>Service: Маршрутизация по правилам
    Service->>Pod: Балансировка на Pod
    
    alt Запрос к БД
        Pod->>DB: SELECT * FROM users
        DB-->>Pod: Данные пользователей
    else Health Check
        Note over Pod: Ответ без БД
    end
    
    Pod-->>Service: HTTP 200 OK
    Service-->>Ingress: HTTP 200 OK
    Ingress-->>Browser: HTTP 200 OK (JSON)
    Browser-->>User: Отображение результата
```
    



## 📦 Требования

- **Docker Desktop** с включенным Kubernetes
- **Helm 3+**
- **kubectl**
- **Git** (для клонирования)

### Проверка установки

powershell
docker --version
kubectl version
helm version


## Быстрый старт
 Подготовка окружения
 Убедитесь, что Kubernetes включен в Docker Desktop
 Settings → Kubernetes → Enable Kubernetes → Apply & Restart

 Проверка кластера

kubectl cluster-info

kubectl get nodes

Установка Ingress Controller
 Добавление репозитория

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm repo update

 Установка Ingress Controller

helm install nginx-ingress ingress-nginx/ingress-nginx `

  -n ingress-nginx `

  --create-namespace

 Проверка

kubectl get pods -n ingress-nginx

kubectl get svc -n ingress-nginx



Установка PostgreSQL через Helm

helm install postgres-test oci://registry-1.docker.io/bitnamicharts/postgresql `
  
-n health-service `
  
--create-namespace `
  
-f values.yaml

Проверка

kubectl get pods -n health-service

Развертывание приложения
Применение всех манифестов

kubectl apply -f k8s/

Или по порядку:

kubectl apply -f k8s/namespace.yaml

kubectl apply -f k8s/configmap.yaml

kubectl apply -f k8s/secret.yaml

kubectl apply -f k8s/service.yaml

kubectl apply -f k8s/job-migration.yaml

kubectl apply -f k8s/deployment.yaml

kubectl apply -f k8s/ingress.yaml

Проверка статуса миграции

kubectl logs -n health-service job/db-migration

Проверка таблиц в БД

kubectl exec -n health-service pod/postgres-test-postgresql-0 -- `
  env PGPASSWORD=securePassword123 psql -U app_user -d app_db -c "\dt"


Настройка Ingress

Вариант А: Через NodePort
Получите NodePort

$NODE_PORT = kubectl get svc -n ingress-nginx nginx-ingress-ingress-nginx-controller `
  -o jsonpath="{.spec.ports[0].nodePort}"

Write-Host "Используйте порт: $NODE_PORT" -ForegroundColor Cyan
Write-Host "Доступ: http://arch.homework:$NODE_PORT/health" -ForegroundColor Yellow

Вариант Б: Через Port-Forward
В ОТДЕЛЬНОМ терминале

kubectl port-forward -n ingress-nginx svc/nginx-ingress-ingress-nginx-controller 80:80
 
Настройка hosts файла
 Добавьте в C:\Windows\System32\drivers\etc\hosts:
 127.0.0.1 arch.homework

##  API Эндпоинты
В директории ets collection.json

