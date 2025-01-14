# SpringBoot Backend Login V2

This is the authentication backend for the Sousa project, built using Java and Spring Boot.  
The project implements token-based authentication for securing API endpoints.

## Features

- User registration
- User authentication with JWT
- Token refresh functionality
- Password change management
- **New in V2:**
  - Email verification functionalities when creating new accounts
  - Implementing a check if an email is already in use upon registration
  - Prevent login without email verified check
  - New endpoint for email verification:
    ```java
    @LogIpIntoTable
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        emailService.verifyEmail(token);
        return ResponseEntity.ok("Email verificado com sucesso!");
    }
    ```
  - Messages translations for exceptions (more types of exceptions)
  - Logs table to track every action in the system into a table in the database (which user did what and when) with exceptions being recorded too.
  - IP address tracking for every request (in the logs table). **Careful with GDPR.**
  - Added message interceptors to personalize exception errors.

## Technologies Used

- **Java**: Primary programming language
- **Spring Boot**: Framework for building robust web applications
- **JPA**: For data persistence
- **Jakarta Validation**: For input validation
- **Lombok**: To reduce boilerplate code
- **Spring Security**: For implementing JWT-based security

## API Endpoints

### Authentication

- **POST** `/api/v1/auth/register`  
  Registers a new user.  
  **Request body:**
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```

- **POST** `/api/v1/auth/authenticate`  
  Authenticates a user and returns a JWT.  
  **Request body:**
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```

- **POST** `/api/v1/auth/refresh-token`  
  Refreshes the authentication token.  
  **Headers:**
  - `Authorization: Bearer <refresh-token>`

- **POST** `/api/v1/auth/verify-email`  
  Verifies the user's email using a token.  
  **Request parameters:**
  - `token: string`

### User Management

- **PATCH** `/api/v1/auth/users/change-password`  
  Updates the user's password.  
  **Request body:**
  ```json
  {
    "oldPassword": "string",
    "newPassword": "string"
  }
  ```

## Security

The application uses **JWT (JSON Web Tokens)** for authentication.  
Each secured endpoint requires an `Authorization` header with a valid JWT.

- **JWT Structure:**
  - Header: Algorithm and token type
  - Payload: User information and claims
  - Signature: Ensures the token's integrity

- **Token Flow:**
  1. **Registration:** The user registers with a username and password.
  2. **Authentication:** The user sends credentials to receive a JWT.
  3. **Authorization:** The user includes the JWT in the `Authorization` header for secured endpoints.
  4. **Token Refresh:** Users can refresh their tokens before expiration.

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repository.git
   cd sousa-back-login
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. The server will be available at `http://localhost:8080`.

## Requirements

- Java 17+
- Maven 3.6+
- PostgreSQL (or your database of choice)

## Contributing

1. Fork the repository.
2. Create a new branch: `git checkout -b feature-name`.
3. Commit your changes: `git commit -m "Add feature-name"`.
4. Push to the branch: `git push origin feature-name`.
5. Open a pull request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
