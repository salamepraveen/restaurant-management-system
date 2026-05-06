# Platform Admin Architecture

This document outlines the architecture and implementation details for the `PLATFORM_ADMIN` role and the system-wide ban functionality within the `pizza-restaurant` microservices ecosystem.

## Overview
The `PLATFORM_ADMIN` is a super-admin role with the authority to oversee all users and restaurants across the system. Specifically, this role has the ability to ban or unban users and restaurants, preventing them from logging into the platform or taking action.

## 1. Data Model Changes (`user-service`)
Two key entities have been updated to support banning:
*   **User (`com.prav.user.model.User`)**: Added a boolean field `isBanned` (default: `false`).
*   **Restaurant (`com.prav.user.model.Restaurant`)**: Added a boolean field `isBanned` (default: `false`).
*   **UserDTO (`com.prav.user.dto.UserDTO` & `com.prav.auth.dto.UserDTO`)**: Updated to transport the `isBanned` state across service boundaries.

## 2. Authentication Flow (`auth-service`)
The `auth-service` acts as the gatekeeper for banned users.

### Platform Admin Login Bypass
The `PLATFORM_ADMIN` credentials are not stored in the database. Instead, they are injected via application properties (`application.properties`):
*   `platform.admin.username`
*   `platform.admin.password`
*   `platform.admin.email`

When a user attempts to log in, `AuthService.signin()` intercepts the request. If the credentials match the platform admin properties exactly, a JWT is immediately issued with the `PLATFORM_ADMIN` role, bypassing the database lookup.

### Enforcing Bans
During both standard password-based login (`signin()`) and OTP-based login (`verifyLoginOtp()`), the `auth-service` retrieves the user's profile from the `user-service`. If `user.isBanned()` is true, an `InvalidCredentialsException` is thrown with the message *"Your account has been banned by the platform administrator"*.

## 3. API Endpoints (`user-service`)
The `user-service` exposes administrative endpoints. These are strictly for retrieving all entities or toggling their ban status.

*   `GET /users/all`: Retrieves a full list of all registered users.
*   `GET /users/admin/restaurants`: Retrieves all restaurants.
*   `PUT /users/ban/{userId}`: Toggles the ban status of a specific user.
*   `PUT /users/restaurant/ban/{restaurantId}`: Toggles the ban status of a specific restaurant.

*Note: All endpoints enforce a check `if (!ROLE_PLATFORM_ADMIN.equals(role))` to prevent unauthorized execution.*

## 4. API Gateway Security (`api-gateway`)
The `api-gateway` enforces role-based access control (RBAC) at the perimeter.
In `SecurityConfig.java`, specific route matchers ensure that only requests containing a valid JWT with the `PLATFORM_ADMIN` role can access the administration endpoints:
```java
.pathMatchers(HttpMethod.GET, "/users/all").hasRole(ROLE_PLATFORM_ADMIN)
.pathMatchers(HttpMethod.GET, "/users/admin/restaurants").hasRole(ROLE_PLATFORM_ADMIN)
.pathMatchers(HttpMethod.PUT, "/users/ban/**").hasRole(ROLE_PLATFORM_ADMIN)
.pathMatchers(HttpMethod.PUT, "/users/restaurant/ban/**").hasRole(ROLE_PLATFORM_ADMIN)
```

## 5. Frontend Integration (`restaurant-app`)
### Routing and Guards
A dedicated Angular component (`PlatformAdminDashboardComponent`) exists at the `/platform-admin` route.
The frontend `Role` guard (`role.ts`) has been updated to explicitly handle the `PLATFORM_ADMIN` fallback, preventing regular `ADMIN` (restaurant administrators) from accessing the platform administration dashboard.

### Dashboard Operations
The dashboard displays tabular data for all users and restaurants. It dynamically issues HTTP `PUT` requests to the `user-service` endpoints to toggle ban statuses in real-time, updating the UI accordingly. Banned users are highlighted with red badges, while active users have green badges.
