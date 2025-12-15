# Session 20 - Keycloak Integration

A Spring Boot application demonstrating OAuth2/OIDC authentication and authorization using Keycloak as the identity provider.

## Overview

This project showcases how to integrate Spring Boot with Keycloak for secure API access using JWT tokens. It implements role-based access control (RBAC) with custom JWT claim extraction from Keycloak's realm access structure.

## Features

- OAuth2 Resource Server integration with Keycloak
- JWT token validation and authentication
- Role-based access control (RBAC)
- Custom JWT authority converter for Keycloak realm roles
- Protected REST endpoints with different access levels

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Keycloak server (running on `http://localhost:8080`)

## Technology Stack

- **Spring Boot**: 3.4.12
- **Spring Security**: OAuth2 Resource Server
- **Java**: 21
- **Maven**: Build tool
- **Keycloak**: Identity and Access Management

## Project Structure

```
src/main/java/org/example/session20keycloak/
├── Session20KeycloakApplication.java  # Main application class
├── SecurityConfig.java                # Security configuration
├── JwtAuthConverter.java              # Custom JWT to authorities converter
└── DemoController.java                # REST controller with demo endpoints
```

## Configuration

### Application Configuration

The application is configured via `application.yml`:

```yaml
server:
  port: 9090
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/demo-realm
```

### Keycloak Setup

1. Start Keycloak server on `http://localhost:8080`
2. Create a realm named `demo-realm`
3. Create a client for this application
4. Configure users with roles:
   - `ADMIN` role
   - `USER` role

## API Endpoints

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/public` | GET | Public | No authentication required |
| `/user/hello` | GET | USER, ADMIN | Requires USER or ADMIN role |
| `/admin/hello` | GET | ADMIN | Requires ADMIN role only |

## Security Configuration

The application uses Spring Security with:

- **OAuth2 Resource Server**: Validates JWT tokens from Keycloak
- **Custom JWT Converter**: Extracts roles from Keycloak's `realm_access.roles` claim
- **Method Security**: Enabled for method-level authorization
- **Role-based Access Control**: Endpoints protected by roles

### JWT Authority Conversion

The `JwtAuthConverter` class extracts roles from the JWT token's `realm_access` claim and converts them to Spring Security authorities with the `ROLE_` prefix.

## Running the Application

1. **Start Keycloak**:
   ```bash
   # Make sure Keycloak is running on http://localhost:8080
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

   Or using the JAR:
   ```bash
   java -jar target/session20-keycloak-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application**:
   - Application runs on `http://localhost:9090`

## Testing the API

### Public Endpoint (No Authentication)
```bash
curl http://localhost:9090/public
```

### Protected Endpoints (Requires JWT Token)

1. **Get an access token from Keycloak**:
   ```bash
   # Use Keycloak's token endpoint or admin console
   # Example using curl:
   curl -X POST http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \
     -d "client_id=your-client-id" \
     -d "username=your-username" \
     -d "password=your-password" \
     -d "grant_type=password"
   ```

2. **Call protected endpoints with the token**:
   ```bash
   # User endpoint
   curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     http://localhost:9090/user/hello

   # Admin endpoint
   curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     http://localhost:9090/admin/hello
   ```

## Dependencies

- `spring-boot-starter-oauth2-resource-server`: OAuth2 resource server support
- `spring-boot-starter-security`: Spring Security framework
- `spring-boot-starter-web`: Spring Web MVC
- `spring-security-test`: Security testing utilities

## Development

### Building
```bash
./mvnw clean package
```

### Running Tests
```bash
./mvnw test
```

## Notes

- The application expects Keycloak to be running on `http://localhost:8080`
- The realm name is configured as `demo-realm` in the application properties
- JWT tokens must include the `realm_access.roles` claim for role-based authorization to work
- All roles are automatically prefixed with `ROLE_` and converted to uppercase

## License

This project is for educational/demonstration purposes.

