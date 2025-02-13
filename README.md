# books-management-app
Приложение, которое предоставляет возможность:
Создать книгу со следующими параметрами: название, автор, жанр, краткое описание и сохранить ее в БД.
Редактировать уже существующую книгу.
Удалить книгу.
Получать информацию о всех сохраненных книгах.
Загружать изображения для книги размером до 1 гигабайта

Приложение состоит из нескольких микросервисов: books, users, files

Информация хранится в базах данных PostgreSQL, отдельных для каждого сервиса books и users
В сервисе files используется MongoDB

Все сервисы и базы данных для них запускаются в Docker контейнерах

Реализован API по адресу http://localhost:8080/api/v1/books
Поддерживаютя HTTP запросы с методами GET, POST, PUT, DELETE
Для запросов с методами POST и PUT требуется тело запроса с полями
```json
{
"title": ,
"authors": [
{
"name": 
},
"genres": [
{
"title": 
}
],
"description": 
}
```
Для обновления существующей книги методом PUT дополнительно в теле требуется поле id со значением id обновляемой книги

Для метода DELETE нужна переменная пути /{id} - id удаляемой книги

По адресу http://localhost:8080/api/v1/files/books/{id}/image можно загрузить или выгрузить изображение для книги по id книги
Поддерживаются изображения размером до 1 Гб
Внутри приложения изображения хранятся в БД MongoDB используя GridFS

По адресу http://localhost:8080/api/v1/auth/register реализована регистрация пользователей, 
тело запроса должно быть вида:
```json
{
"username": ,
"password": 
}
```
Длина имени минимум 2 символа, пароля минимум 8

По адресу http://localhost:8080/api/v1/auth/login реализована аутентификация пользователей,
тело запроса должно быть вида:
```json
{
"username": ,
"password": 
}
```
Длина имени минимум 2 символа, пароля минимум 8

## Дальнейшая разработка
### Перед тем как начать разработку, у вас должны быть установлены следующие зависимости:
- Java 17
- Docker
- для авторизации в переменную окружения JWT_SECRET нужно прописать свой секрет для Jwt длиной 64 байта закодированный
в Base64, в application.properties в jwt.secret прописан дефолтный секрет для Jwt длиной 64 байта,
закодированный в Base64.
- кроме того должны быть установлены следующие переменные окружения:
  - CONFIG_SERVICE_PASSWORD - пароль для доступа к сервису хранилища конфигураций config-server
  - MONGODB_DB_NAME - имя базы данных в MongoDB
  - MONGODB_USER - имя пользователя MongoDB
  - MONGODB_PASSWORD - пароль для MongoDB
  - POSTGRE_BOOKS_DB_NAME - имя базы PostgreSQL для сервиса books
  - POSTGRE_BOOKS_USER - имя пользователя PostgreSQL для сервиса books
  - POSTGRE_BOOKS_PASS - пароль PostgreSQL для сервиса books
  - POSTGRE_USERS_DB_NAME - имя базы PostgreSQL для сервиса users
  - POSTGRE_USERS_USER - имя пользователя PostgreSQL для сервиса users
  - POSTGRE_USERS_PASS - пароль PostgreSQL для сервиса users
  - CONSUL_SERVER_ROLE - тэг для сервисов при их регистрации в Spring Cloud Consul
  - USERS_DB_CONTAINER_NAME - имя контейнера с БД Postgres для сервиса users
  - BOOKS_DB_CONTAINER_NAME - имя контейнера с БД Postgres для сервиса books

### Сборка и запуск приложения
- Для сборки запустите из корня
  ```bash
  ./gradlew bootJar

- Для отладки и разработки запустите сначала необходимые контейнеры
  ```bash
  ./gradlew startContainersForDebugAndDevelopment

Затем нужно запустить ConfigServerApplication, GatewayApplication
После этого можно или запускать необходимые сервисы для отладки (BookServiceApplication, UserServiceApplication,
JwtServiceApplication, FileServiceApplication) или можно запустить конфигурацию RunAllDependableServices, 
которая запустит их все сразу
  

- Для запуска всех сервисов в контейнерах - симуляции прода
  ```bash
  ./gradlew startProdContainers

- Для остановки всех контейнеров
  ```bash
  ./gradlew stopContainers
  
Отслеживать сервисы, зарегистрированные в Spring Cloud Consul можно на http://localhost:8500/ui/dc1/services