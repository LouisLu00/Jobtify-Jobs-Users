# Jobtify-Users-Microservice

The **Jobtify Users Microservice** is a Spring Boot-based microservice for user management within the Jobtify application. It is hosted on AWS and provides endpoints for registering users, logging in, retrieving user details, and checking if a user exists.

## OpenAPI Documentation
The project uses **Springdoc OpenAPI** for generating API documentation. To access OpenAPI Documentation, we can use the following link:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## UserController Overview

The `UserController` provides the following endpoints:

- **`POST /api/users/register`**: Register a new user.
    - **Request Body**: `{ "username": "string", "password": "string", "email": "string" }`
    - **Response**: `201 Created` with user details or `400 Bad Request` on failure.

- **`POST /api/users/login`**: Authenticate a user for login.
    - **Request Body**: `{ "username": "string", "password": "string" }`
    - **Response**: `200 OK` with user details or `401 Unauthorized` if credentials are invalid.

- **`GET /api/users/{id}`**: Retrieves user information by ID.
    - **Response**: `200 OK` with user details or `404 Not Found` if no user exists with the specified ID.

- **`GET /api/users/{userId}/exists`**: Checks if a user exists by ID.
    - **Response**: `200 OK` with `true` if the user exists, `404 Not Found` if not.

## Installation and Setup
1. **Clone and navigate to the repository**:
 ```bash
 git clone https://github.com/LouisLu00/Jobtify-Users-Microservice.git
cd Jobtify-Users-Microservice
```
2. **Build and run the Application:**
```bash
mvn clean install
mvn spring-boot:run
```
