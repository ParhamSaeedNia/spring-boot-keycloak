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

1. **Start Keycloak server** on `http://localhost:8080`

2. **Create a realm** named `demo-realm`

3. **Create a client** for this application:
   - Client ID: `spring-boot-app` (or any name you prefer)
   - Client Protocol: `openid-connect`
   - Access Type: `public` or `confidential`
   - **Important**: Enable "Direct Access Grants" (for password grant type)
   - Valid Redirect URIs: `http://localhost:9090/*`

4. **Create roles** in the realm:
   - `ADMIN` role
   - `USER` role

5. **Create users** and assign roles:
   - Create a user (e.g., `testuser`)
   - Set a password
   - Assign roles: `USER` or `ADMIN`

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

## Troubleshooting

### Error: "invalid_client" or "Invalid client credentials"

This error occurs when:
1. The client doesn't exist in Keycloak
2. The client_id is incorrect
3. Direct Access Grants is not enabled
4. Client secret is missing (for confidential clients)

**Solution - Step by Step**:

1. **Access Keycloak Admin Console**:
   - Go to `http://localhost:8080`
   - Login with admin credentials
   - Select the `demo-realm` realm

2. **Create a Client**:
   - Go to **Clients** → **Create client**
   - **Client ID**: `spring-boot-app` (or any name you choose)
   - **Client Protocol**: `openid-connect`
   - Click **Next**

3. **Configure Client Settings**:
   - **Access Type**: Select `public` (for testing) or `confidential` (for production)
   - **Direct Access Grants Enabled**: **ON** (This is critical!)
   - **Valid Redirect URIs**: `http://localhost:9090/*`
   - Click **Save**

4. **If using Confidential Client**:
   - Go to the **Credentials** tab
   - Copy the **Client Secret**
   - Use it in your token request

5. **Verify the Client ID**:
   - The Client ID should match what you use in the curl command
   - Default Keycloak clients like `account`, `admin-cli`, `broker`, etc. may not work for password grant

6. **Test with the correct client_id**:
   ```bash
   # Replace 'spring-boot-app' with your actual client ID
   curl -X POST http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "client_id=spring-boot-app" \
     -d "username=admin" \
     -d "password=admin" \
     -d "grant_type=password"
   ```

### Error: "Invalid user credentials"

- Verify the username and password are correct
- Make sure the user exists in the `demo-realm` realm (not the master realm)
- Check if the user account is enabled
- Verify the user has a password set

### Error: "Direct access grants disabled"

- Go to your client settings in Keycloak
- Enable "Direct Access Grants" toggle
- Save the changes

## Testing the API

### Public Endpoint (No Authentication)
```bash
curl http://localhost:9090/public
```

### Protected Endpoints (Requires JWT Token)

#### Step 1: Get an Access Token from Keycloak

**Using Password Grant (Direct Access Grant)**:

```bash
# Replace 'spring-boot-app' with your actual client ID from Keycloak
# Replace 'testuser' and 'your-password' with actual user credentials
curl -X POST http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=spring-boot-app" \
  -d "username=testuser" \
  -d "password=your-password" \
  -d "grant_type=password"
```

**Important**: 
- The `client_id` must match a client you created in Keycloak
- The client must have "Direct Access Grants" enabled
- The user must exist in the `demo-realm` realm

**Response Example**:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "...",
  "token_type": "Bearer",
  "not-before-policy": 0,
  "session_state": "...",
  "scope": "profile email"
}
```

**Extract the access token**:
```bash
# Save the response and extract the token
TOKEN=$(curl -s -X POST http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=spring-boot-app" \
  -d "username=testuser" \
  -d "password=your-password" \
  -d "grant_type=password" | jq -r '.access_token')

echo $TOKEN
```

**Note**: If using `confidential` client type, you'll also need to include `client_secret`:
```bash
curl -X POST http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=spring-boot-app" \
  -d "client_secret=your-client-secret" \
  -d "username=testuser" \
  -d "password=your-password" \
  -d "grant_type=password"
```

#### Step 2: Call Protected Endpoints with the Token

```bash
# User endpoint (requires USER or ADMIN role)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:9090/user/hello

# Admin endpoint (requires ADMIN role only)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:9090/admin/hello
```

**Complete Example**:
```bash
# Get token and make request in one go
TOKEN=$(curl -s -X POST http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=spring-boot-app" \
  -d "username=testuser" \
  -d "password=your-password" \
  -d "grant_type=password" | jq -r '.access_token')

curl -H "Authorization: Bearer $TOKEN" http://localhost:9090/user/hello
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
- **Password Grant Type**: Make sure "Direct Access Grants" is enabled in your Keycloak client settings
- **Token Expiration**: Access tokens typically expire after 5 minutes (300 seconds). Use refresh tokens to get new access tokens
- For production, consider using Authorization Code flow instead of password grant for better security

## License

This project is for educational/demonstration purposes.

