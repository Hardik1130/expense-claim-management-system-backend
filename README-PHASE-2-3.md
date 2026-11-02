# Expense Claim Management - Phase 2 & 3

This package extends the Phase 1 foundation with database entities/repositories and JWT authentication.

## Important integration note
Replace the Phase 1 source files with the matching files in this package. Keep the existing package root `com.expensemanagement`.

The Phase 1 `PhaseOneSecurityConfig.java` must be removed because this package provides the JWT security filter chain. The system ping/status endpoints are included again and use your `ResponseEntity<Response>` convention. Keep the new `Response.java` and `ApiErrorResponse.java`. Remove the old `ApiResponse.java` only if no remaining class imports it.

## Environment variables (PowerShell)
```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="YOUR_POSTGRES_PASSWORD"
$bytes = [byte[]](1..32 | ForEach-Object { Get-Random -Maximum 256 })
$env:JWT_SECRET_B64 = [Convert]::ToBase64String($bytes)
$env:MANAGER_NAME="System Manager"
$env:MANAGER_EMAIL="manager@example.com"
$env:MANAGER_PASSWORD="Set-A-Strong-Manager-Password"
```
Keep the JWT secret and manager password private. Use a persistent secret, do not generate a new JWT secret every time the application starts.

## APIs
- POST `/api/v1/auth/register`
- POST `/api/v1/auth/login`
- POST `/api/v1/auth/refresh`
- POST `/api/v1/auth/logout`
- GET `/api/v1/auth/me` (Bearer access token)

Registration always creates an EMPLOYEE. Role is not accepted from request JSON.

## Register example
```json
{
  "name": "Aman Sharma",
  "email": "aman@example.com",
  "password": "Password@123"
}
```

## Login example
```json
{
  "email": "aman@example.com",
  "password": "Password@123"
}
```

## Refresh example
```json
{
  "refreshToken": "COPY_REFRESH_TOKEN_FROM_LOGIN_RESPONSE"
}
```

## Logout example
```json
{
  "refreshToken": "COPY_CURRENT_REFRESH_TOKEN"
}
```

## Protected request
`GET /api/v1/auth/me`

Header:
`Authorization: Bearer ACCESS_TOKEN`

## Token behavior
- Access token is a signed JWT, default lifetime 15 minutes.
- Refresh token is a cryptographically random opaque token, default lifetime 7 days.
- Only SHA-256 hash of refresh token is persisted.
- Refresh rotates the refresh token: old token is revoked and a new pair is returned.
- Logout revokes the supplied refresh token.
- Deactivated accounts cannot authenticate; the JWT filter reloads user details on each request and rejects disabled users.

## Security note
This is the Phase 2/3 foundation. Claim-specific endpoints, ownership checks, approval rules, file storage service, audit service and dashboards are not implemented in this package yet. They will be added in subsequent implementation phases.

## Build
```bash
mvn clean test
mvn spring-boot:run
```
