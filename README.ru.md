# Books Management App

## Описание
Books Management App — приложение для управления книгами, которое предоставляет возможности:
- Создавать книги с параметрами: название, автор, жанр, краткое описание.
- Редактировать уже существующие книги.
- Удалять книги.
- Получать информацию о всех сохраненных книгах.
- Загружать изображения для книг размером до 1 гигабайта.

Приложение состоит из нескольких микросервисов:
- `Books Service`: управление книгами, хранение данных в PostgreSQL.
- `Users Service`: регистрация и аутентификация пользователей, хранение данных в PostgreSQL.
- `Files Service`: загрузка и хранение изображений книг, данные хранятся в MongoDB (GridFS).

### Асинхронное взаимодействие
С помощью Kafka реализовано асинхронное взаимодействие между сервисами:
- При удалении книги из сервиса `books` автоматически удаляется связанный с ней файл в сервисе `files`.
- При удалении файла в сервисе `files` обнуляется ссылка на файл в книгах.
- При изменении файла в сервисе `files` обновляется ссылка на него в книгах, если у книги был уже файл, то он удаляется из БД.

## Запросы API

### Books API
- **URL**: `http://localhost:8080/api/v1/books`
    - **GET**: Получить список всех книг.
    - **POST**: Создать книгу.
    - **PUT**: Обновить книгу по ID.
        - Тело запроса:
          ```json
          {
            "id": ,
            "title": ,
            "authors": [
              {"name": }
            ],
            "genres": [
              {"title": }
            ],
            "description": 
          }
          ```
    - **DELETE**: Удалить книгу по ID.

### Files API
- **URL**: `http://localhost:8080/api/v1/files/books/{id}/image`
    - **POST**: Загрузить изображение для книги по ID (до 1 Гб).
    - **GET**: Получить изображение книги по ID.
    - **DELETE**: Удалить изображение книги по ID.

### Auth API
- **URL**: `http://localhost:8080/api/v1/auth/register`
    - **POST**: Регистрация нового пользователя.
        - Тело запроса:
          ```json
          {
            "username": ,
            "password": 
          }
          ```
- **URL**: `http://localhost:8080/api/v1/auth/login`
    - **POST**: Аутентификация пользователя.
        - Тело запроса:
          ```json
          {
            "username": ,
            "password": 
          }
          ```

## Технические требования
Перед началом разработки установите:
- Java 17
- Docker
- Docker Compose
- Minikube (для локального деплоя Kubernetes)
- Helm (для установки чапртов Kubernetes)

Также создайте и настройте переменные окружения:
- `JWT_SECRET`: секрет для JWT (длина 64 байта, закодированный в Base64).
- `CONFIG_SERVICE_PASSWORD`: пароль для сервиса конфигураций.
- `MONGODB_DB_NAME`, `MONGODB_USER`, `MONGODB_PASSWORD`: данные для MongoDB.
- `POSTGRE_BOOKS_DB_NAME`, `POSTGRE_BOOKS_USER`, `POSTGRE_BOOKS_PASS`: данные для PostgreSQL (books).
- `POSTGRE_USERS_DB_NAME`, `POSTGRE_USERS_USER`, `POSTGRE_USERS_PASS`: данные для PostgreSQL (users).
- `CONSUL_SERVER_ROLE`, `USERS_DB_CONTAINER_NAME`, `BOOKS_DB_CONTAINER_NAME`, `KAFKA_CONTAINER_NAME`: контейнеры и роль для Consul.

## Сборка и запуск

### Сборка приложения
```bash
./gradlew bootJar
```
### Тестирование
Перед запуском интеграционных тестов нужно собрать приложение, как описано выше, так как для тестов используется TestContainers

Для модуля 'books-service' добавлены юнит и интеграционные тесты. Чтобы запустить тесты, выполните стандартную задачу Gradle:
```bash
./gradlew test
```
Эта команда выполнит как юнит, так и интеграционные тесты для проверки работы 'books-service'

Тесты для всех остальных модулей будут добавлены в следующих обновлениях.

### Запуск для разработки
1. Запустите необходимые контейнеры:
    ```bash
    ./gradlew startContainersForDebugAndDevelopment
    ```
2. Запустите необходимые конфигурации:
    - ConfigServerApplication
    - GatewayApplication
3. Запустите сервисы:
    - BookServiceApplication
    - UserServiceApplication
    - JwtServiceApplication
    - FileServiceApplication
   
      Или используйте конфигурацию RunAllDependableServices, чтобы запустить их одновременно.
### Запуск в контейнерах (прод)
```bash
./gradlew startProdContainers
```
### Остановка контейнеров
```bash
./gradlew stopContainers
```
### Мониторинг Consul
Мониторинг зарегистрированных сервисов доступен по адресу: http://localhost:8500/ui/dc1/services.
Для доступа к мониторингу нужно использовать токен, сгенерированный в ./consul_data при старте Consul

## Деплой в Kubernetes

В проект добавлены манифесты Kubernetes, находящиеся в папке k8s/.
Файл secrets.example.yaml нужно переименовать в secrets.yaml и значения всех секретов в нем должны быть заполнены.

Вы можете развернуть сервисы двумя способами:

### Установка отдельных манифестов

Для установки отдельных сервисов используйте следующую команду для каждого файла:

```bash
kubectl apply -f k8s/<manifest-filename>.yaml
```

Пример:
```bash
kubectl apply -f k8s/book-service-deployment.yaml
```
### Установка всех компонентов сразу через Kustomize
Чтобы установить все сервисы и конфигурации сразу, используя kustomization.yaml, выполните:
```bash
kubectl apply -k k8s/
```
### Мониторинг с помощью Prometheus
Также добавлен файл microservices-servicemonitor.yaml, который можно установить для сбора метрик с эндпойнтов /actuator/prometheus всех микросервисов:
```bash
kubectl apply -f k8s/microservices-servicemonitor.yaml
```
Убедитесь, что в вашем кластере установлен Prometheus и CRD ServiceMonitor (например, через Prometheus Operator), чтобы эта функция работала корректно.

#### Prometheus и Grafana можно устанвить с помощью Helm
```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
kubectl create namespace monitoring
helm install prometheus prometheus-community/kube-prometheus-stack -n monitoring
```