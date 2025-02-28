# Books Management App
[English](README.md) | [Русский](README.ru.md)
## Description
Books Management App is a book management application that provides the following features:
- Create books with attributes: title, author, genre, and short description.
- Edit existing books.
- Delete books.
- Retrieve information about all saved books.
- Upload book images up to 1 GB in size.

The application consists of several microservices:
- `Books Service`: Manages books and stores data in PostgreSQL.
- `Users Service`: Handles user registration and authentication, stores data in PostgreSQL.
- `Files Service`: Manages uploading and storing book images, data is stored in MongoDB (GridFS).

### Asynchronous Communication
Kafka is used to implement asynchronous communication between services:
- When a book is deleted from the books service, the related file in the files service is automatically deleted.
- When a file is deleted in the files service, the file reference is cleared in the books service.
- When a file is updated in the files service, the reference to the file is updated in the books service.
- When a file is updated in the files service and book has file related to it, then that old file is deleted

## API Requests

### Books API
- **URL**: `http://localhost:8080/api/v1/books`
  - **GET**: Retrieve the list of all books.
  - **POST**: Create a new book.
  - **PUT**: Update a book by ID.
    - Request body:
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
  - **DELETE**: Delete a book by ID.

### Files API
- **URL**: `http://localhost:8080/api/v1/files/books/{id}/image`
  - **POST**: Upload an image for a book by ID (up to 1 GB).
  - **GET**: Retrieve the image for a book by ID.
  - **DELETE**: Delete the image for a book by ID.

### Auth API
- **URL**: `http://localhost:8080/api/v1/auth/register`
  - **POST**: Register a new user.
    - Request body:
      ```json
      {
        "username": ,
        "password": 
      }
      ```
- **URL**: `http://localhost:8080/api/v1/auth/login`
  - **POST**: Authenticate a user.
    - Request body:
      ```json
      {
        "username": ,
        "password": 
      }
      ```

## Technical Requirements
Before starting development, make sure to have:
- Java 17
- Docker
- Docker Compose

Additionally, configure the following environment variables:
- `JWT_SECRET`: secret for JWT (64-byte length, Base64 encoded).
- `CONFIG_SERVICE_PASSWORD`: password for the config service.
- `MONGODB_DB_NAME`, `MONGODB_USER`, `MONGODB_PASSWORD`: MongoDB credentials.
- `POSTGRE_BOOKS_DB_NAME`, `POSTGRE_BOOKS_USER`, `POSTGRE_BOOKS_PASS`: PostgreSQL (books) credentials.
- `POSTGRE_USERS_DB_NAME`, `POSTGRE_USERS_USER`, `POSTGRE_USERS_PASS`: PostgreSQL (users) credentials.
- `CONSUL_SERVER_ROLE`, `USERS_DB_CONTAINER_NAME`, `BOOKS_DB_CONTAINER_NAME`, `KAFKA_CONTAINER_NAME`: containers and Consul role.

## Build and Run

### Build the Application
```bash
./gradlew bootJar
```

### Run for Development

1. Start the required containers:

   ```bash
   ./gradlew startContainersForDebugAndDevelopment
   ```

2. Start the required configurations:

    - ConfigServerApplication
    - GatewayApplication

3. Start the services:

    - BookServiceApplication
    - UserServiceApplication
    - JwtServiceApplication
    - FileServiceApplication
   
      Or use the RunAllDependableServices configuration to start them simultaneously.
### Run in Containers (Production)
```bash
./gradlew startProdContainers
```
### Stop Containers
```bash
./gradlew stopContainers
```
### Consul Monitoring
Registered services can be monitored at: http://localhost:8500/ui/dc1/services.
Access to monitoring requires a token generated in ./consul_data when started Consul.