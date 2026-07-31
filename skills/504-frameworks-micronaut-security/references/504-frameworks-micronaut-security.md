---
name: 504-frameworks-micronaut-security
description: Use when you need to design, review, or improve security in Micronaut applications — including micronaut-security authentication, @Secured and intercept-url-map rules, JWT/session strategies, SecurityService checks, CORS, CSRF awareness for browser apps, rejection handlers, and sensitive-data-safe logging.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut Security Guidelines

## Role

You are a Senior software engineer with extensive experience in Micronaut Security and secure API design

## Goal

Apply secure-by-default Micronaut security practices: authenticate requests consistently, authorize with explicit role or expression rules, avoid custom header secrets, configure CORS narrowly, and prevent credential and token leakage in logs and responses. Align with `@502-frameworks-micronaut-rest` for HTTP error bodies.

## Constraints

Before applying recommendations, ensure the project compiles. Compilation failure is a blocking condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: @Secured on controllers
- Example 2: Intercept URL map rules
- Example 3: JWT bearer configuration
- Example 4: Custom authentication provider
- Example 5: Micronaut CORS
- Example 6: Authorization failure responses
- Example 7: CSRF and session cookies

### Example 1: @Secured on controllers

Title: Anonymous, authenticated, and role-based routes
Description: Use `SecurityRule.IS_ANONYMOUS`, `IS_AUTHENTICATED`, and role strings (e.g. `ROLE_ADMIN`) on controller classes or methods. Prefer declarative security over manual header checks.

**Good example:**

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED;

@Controller("/api")
@Secured(IS_AUTHENTICATED)
class ApiController {

    @Secured(IS_ANONYMOUS)
    @Get("/health")
    String health() { return "ok"; }

    @Get("/profile/{id}")
    String profile(@PathVariable String id) { return id; }

    @Secured("ROLE_ADMIN")
    @Delete("/admin/cache")
    void clearCache() { }
}
```

**Bad example:**

```java
@Delete("/admin/cache")
void clear(@Header("X-Admin-Token") String t) {
    if (!"secret".equals(t)) throw new RuntimeException();
}
```

### Example 2: Intercept URL map rules

Title: Pattern-based access in configuration
Description: Define `micronaut.security.intercept-url-map` entries for static path policies (health, swagger UI, admin prefixes). Order patterns from specific to general where the implementation requires it.

**Good example:**

```yaml
micronaut:
  security:
    intercept-url-map:
      - pattern: /health
        httpMethod: GET
        access:
          - isAnonymous()
      - pattern: /api/admin/**
        access:
          - ROLE_ADMIN
      - pattern: /api/**
        access:
          - isAuthenticated()
```

**Bad example:**

```yaml
pattern: /**
  access:
    - isAnonymous()
# Entire application including mutating APIs is public
```

### Example 3: JWT bearer configuration

Title: Signatures, issuers, and secrets from environment
Description: Configure `micronaut.security.token.jwt` with signatures or JWKS. Load secrets via environment variables or Micronaut Kubernetes config — never commit private keys or symmetric secrets.

**Good example:**

```yaml
micronaut:
  security:
    token:
      jwt:
        signatures:
          secret:
            generator:
              secret: "${JWT_GENERATOR_SIGNATURE_SECRET}"
        claims-validators:
          issuer: https://idp.example.com/
```

**Bad example:**

```yaml
secret: "hardcoded-symmetric-key-in-git"
```

### Example 4: Custom authentication provider

Title: HttpRequestAuthenticationProvider with roles; never log credentials
Description: Implement `HttpRequestAuthenticationProvider<B>` (Micronaut 4+) to validate credentials against a secure store and return an `AuthenticationResponse` with principal and roles. Never log passwords or API keys.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

@Singleton
class DbAuthProvider<B> implements HttpRequestAuthenticationProvider<B> {
    @Override
    public AuthenticationResponse authenticate(
            HttpRequest<B> request,
            AuthenticationRequest<String, String> authRequest) {
        // validate credentials against DB here — never log password
        return AuthenticationResponse.success(authRequest.getIdentity());
    }
}
```

**Bad example:**

```java
LOG.info("login attempt password={}", password);
// Credential leakage via logs
```

### Example 5: Micronaut CORS

Title: Restrict allowed origins for browser clients
Description: Enable CORS in `micronaut.server.cors` with explicit origins. Avoid `*` when cookies or credentials are sent.

**Good example:**

```yaml
micronaut:
  server:
    cors:
      enabled: true
      configurations:
        web:
          allowedOrigins:
            - https://app.example.com
          allowedMethods:
            - GET
            - POST
            - PUT
            - DELETE
          allowedHeaders:
            - Authorization
            - Content-Type
```

**Bad example:**

```yaml
allowedOrigins:
  - "*"
allowedCredentials: true
# Invalid or unsafe combination in browsers
```

### Example 6: Authorization failure responses

Title: Distinguish 401 (unauthenticated) from 403 (forbidden) in JSON responses
Description: Register an `ExceptionHandler` for `io.micronaut.security.authentication.AuthorizationException` and inspect `isForbidden()` to return the correct status: 401 for unauthenticated callers and 403 for authenticated callers that lack the required role.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.security.authentication.AuthorizationException;
import jakarta.inject.Singleton;

@Singleton
class AuthorizationExceptionMapper implements ExceptionHandler<AuthorizationException, HttpResponse<?>> {
    @Override
    public HttpResponse<?> handle(HttpRequest request, AuthorizationException e) {
        if (e.isForbidden()) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
                .body(java.util.Map.of("error", "FORBIDDEN"));
        }
        return HttpResponse.status(HttpStatus.UNAUTHORIZED)
            .body(java.util.Map.of("error", "UNAUTHORIZED"));
    }
}
```

**Bad example:**

```java
return HttpResponse.status(403).body(e.toString());
// Always 403 regardless of auth state; leaks exception detail
```

### Example 7: CSRF and session cookies

Title: Browser-facing apps need explicit CSRF strategy
Description: Pure bearer-token APIs are often immune to classic CSRF. If Micronaut serves cookie-based sessions to browsers, enable CSRF protection or use same-site cookies and modern SPA patterns; do not assume `@Secured` alone prevents CSRF.

**Good example:**

```text
Document: cookie session login uses CSRF tokens or SameSite=strict cookies; APIs use Authorization header only.
```

**Bad example:**

```text
Cookie session for SPA + state-changing POST without CSRF defense — vulnerable to cross-site form posts
```


## Output Format

- **ANALYZE** `@Secured` coverage, intercept URL maps, JWT config, CORS, and error responses for authz failures
- **CATEGORIZE** issues: anonymous-by-default, weak JWT secrets, overbroad CORS, missing role checks, information disclosure
- **APPLY** least-privilege rules, secure token configuration, and JSON/Problem responses for 401/403
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- Do not commit signing secrets or API keys to the repository
- Do not log Authorization headers or JWTs
- Re-verify security rules after dependency upgrades to micronaut-security