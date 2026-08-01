---
name: 404-frameworks-quarkus-security
description: Use when you need to design, review, or improve security in Quarkus applications — including Quarkus Security with JWT/OIDC, basic auth, @RolesAllowed / @Authenticated / @PermitAll, SecurityIdentity, permission checks, path-based authorization in configuration, exception mapping for auth failures, and sensitive-data-safe logging.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus Security Guidelines

## Role

You are a Senior software engineer with extensive experience in Quarkus Security and secure API design

## Goal

Apply secure-by-default Quarkus security practices: enforce authentication and authorization consistently at resource and configuration layers, use least-privilege roles, prefer declarative Jakarta Security annotations over ad-hoc header checks, and prevent sensitive data exposure. Align with `@402-frameworks-quarkus-rest` for error responses.

## Constraints

Before applying recommendations, ensure the project compiles. Compilation failure is a blocking condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Declarative security on resources
- Example 2: SecurityIdentity in services
- Example 3: Path-based authorization in configuration
- Example 4: OIDC / JWT bearer validation
- Example 5: Auth failure exception mapping
- Example 6: CORS for browser clients
- Example 7: Logging without secrets

### Example 1: Declarative security on resources

Title: @Authenticated, @RolesAllowed, @PermitAll
Description: Class-level defaults with method-level overrides: mark the resource authenticated by default, then ` @PermitAll` on specific public methods, and `@RolesAllowed` for admin operations.

**Good example:**

```java
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

@Path("/orders")
@Authenticated
public class OrderResource {

    @GET
    @Path("{id}")
    public String get(@PathParam("id") String id) { return id; }

    @GET
    @Path("public/catalog")
    @PermitAll
    public String catalog() { return "ok"; }

    @DELETE
    @Path("{id}")
    @RolesAllowed("admin")
    public void delete(@PathParam("id") String id) { }
}
```

**Bad example:**

```java
@DELETE
@Path("{id}")
public void delete(@PathParam("id") String id, @HeaderParam("X-Token") String t) {
    if (!"secret".equals(t)) throw new ForbiddenException();
}
```

### Example 2: SecurityIdentity in services

Title: Principal name, roles, and attribute checks
Description: Inject `SecurityIdentity` in CDI beans for ownership checks and audit logging. Avoid trusting client-supplied user IDs without comparing to the authenticated principal.

**Good example:**

```java
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderService {

    @Inject
    SecurityIdentity identity;

    public void assertOwner(String ownerUserId) {
        if (!identity.getPrincipal().getName().equals(ownerUserId)) {
            throw new jakarta.ws.rs.ForbiddenException("not owner");
        }
    }
}
```

**Bad example:**

```java
public List<Order> loadForUser(String userIdFromClient) {
    return repo.findByUser(userIdFromClient);
}
// Client can pass any userId unless the resource path or service binds userId to SecurityIdentity
```

### Example 3: Path-based authorization in configuration

Title: quarkus.http.auth.permission.* for coarse routes
Description: Use `quarkus.http.auth.permission` policies for static path patterns (e.g. actuator-like endpoints, admin prefixes). Combine with annotation security for fine-grained control.

**Good example:**

```properties
quarkus.http.auth.permission.public.paths=/health,/q/health
quarkus.http.auth.permission.public.policy=permit

quarkus.http.auth.permission.admin.paths=/api/admin/*
quarkus.http.auth.permission.admin.policy=role:admin
```

**Bad example:**

```properties
quarkus.http.auth.permission.all.paths=/*
quarkus.http.auth.permission.all.policy=permit
# Entire server anonymous
```

### Example 4: OIDC / JWT bearer validation

Title: Configure tenant and issuer; avoid hardcoded secrets
Description: Use `quarkus-oidc` for bearer JWT validation with issuer or JWKS URL from configuration. Map roles from token claims via `quarkus.oidc.roles.role-claim` when needed.

**Good example:**

```properties
quarkus.oidc.auth-server-url=https://idp.example.com/realms/app
quarkus.oidc.client-id=api-backend
# Use TLS; store client secrets via env or Kubernetes secrets, not in Git
```

**Bad example:**

```java
String SHARED_SECRET = "hardcoded";
// Verifying JWT with a symmetric secret committed in source
```

### Example 5: Auth failure exception mapping

Title: Consistent 401/403 JSON without stack traces
Description: Map Quarkus Security exceptions `UnauthorizedException` (unauthenticated → 401) and `ForbiddenException` (authenticated but lacking role → 403) to stable JSON or Problem Details. Log server-side details with correlation IDs only.

**Good example:**

```java
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedMapper implements ExceptionMapper<UnauthorizedException> {
    @Override
    public Response toResponse(UnauthorizedException ex) {
        return Response.status(401)
            .type(MediaType.APPLICATION_JSON)
            .entity(java.util.Map.of("error", "UNAUTHORIZED"))
            .build();
    }
}

@Provider
class ForbiddenMapper implements ExceptionMapper<ForbiddenException> {
    @Override
    public Response toResponse(ForbiddenException ex) {
        return Response.status(403)
            .type(MediaType.APPLICATION_JSON)
            .entity(java.util.Map.of("error", "FORBIDDEN"))
            .build();
    }
}
```

**Bad example:**

```java
return Response.status(401).entity(ex.toString()).build();
// May include exception class names and internal detail; 403 case is unhandled
```

### Example 6: CORS for browser clients

Title: Restrict origins when credentials are used
Description: Configure `quarkus.http.cors` with explicit origins. Avoid `*` when `access-control-allow-credentials` is true.

**Good example:**

```properties
quarkus.http.cors=true
quarkus.http.cors.origins=https://app.example.com
quarkus.http.cors.methods=GET,POST,PUT,DELETE
quarkus.http.cors.headers=Authorization,Content-Type
```

**Bad example:**

```properties
quarkus.http.cors.origins=/.*/
# Overly permissive for credentialed browser sessions
```

### Example 7: Logging without secrets

Title: Never log raw Authorization headers or tokens
Description: Log authentication failures with parameterized logging and opaque correlation IDs. Do not log JWTs, API keys, or passwords.

**Good example:**

```java
org.jboss.logging.Logger LOG = org.jboss.logging.Logger.getLogger(Auth.class);
LOG.warnf("Auth failed requestId=%s", requestId);
```

**Bad example:**

```java
System.out.println("Bearer " + token);
// Secret exfiltration via logs
```


## Output Format

- **ANALYZE** annotations, `application.properties` auth policies, OIDC setup, CORS, and exception mapping for 401/403
- **CATEGORIZE** issues: anonymous-by-default, missing role checks, IDOR via client-supplied identifiers, weak CORS, secret leakage
- **APPLY** least-privilege route and method security, correct OIDC config, and stable auth error bodies
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- Do not ship permissive `permit` policies covering the whole API surface
- Do not trust client-supplied user or tenant IDs without binding to SecurityIdentity
- Rotate and externalize OIDC client secrets and signing keys