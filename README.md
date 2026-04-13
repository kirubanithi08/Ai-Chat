# AI Chatbot Backend

A production-ready Spring Boot backend for an AI Chatbot powered by Google Gemini.

## Features

- **Gemini Integration**: Seamlessly connects to Google Gemini API for intelligent chat responses.
- **Streaming Responses**: Real-time message streaming using Server-Sent Events (SSE).
- **Rate Limiting**: Protects your API with user-based rate limiting (Bucket4j).
- **Security**: JWT-based authentication and secure session management.
- **Standardized API**: Consistent `ApiResponse` wrapper for all endpoints.
- **Documentation**: Interactive API documentation with Swagger UI.
- **Comprehensive Logging**: Detailed structured logging for observability.

## Technologies

- Java 21
- Spring Boot 3.4.x
- Spring Security (JWT)
- Spring Data JPA (PostgreSQL)
- Spring WebFlux (for streaming)
- SpringDoc OpenAPI (Swagger)
- Lombok

## Getting Started

### Prerequisites

- JDK 21 or higher
- PostgreSQL
- Google Gemini API Key

### Configuration

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aichatbot
spring.datasource.username=your_username
spring.datasource.password=your_password

gemini.api-key=YOUR_GEMINI_API_KEY

jwt.secret=your_very_secure_jwt_secret_key_that_is_long_enough
jwt.expiration=86400000
```

### Running the Application

```bash
./mvnw spring-boot:run
```

### API Documentation

Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Create a new account
- `POST /api/auth/login` - Get access token

### Chat
- `POST /api/chat` - Synchronous message delivery
- `POST /api/chat/stream` - Streaming message delivery (SSE)
- `GET /api/chat/sessions` - List user chat history
- `GET /api/chat/{sessionId}/messages` - Get conversation details

## Development

### Running Tests

```bash
./mvnw test
```

## License

This project is licensed under the MIT License.
