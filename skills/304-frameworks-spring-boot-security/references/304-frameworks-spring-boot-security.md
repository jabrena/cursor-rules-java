---
name: 304-frameworks-spring-boot-security
description: Use when you need to design, review, or improve security in Spring Boot applications — including SecurityFilterChain, OAuth2/JWT resource server patterns, form login basics, method security (@PreAuthorize), CSRF and CORS for APIs, session fixation, security headers, exception handling, password encoding, and sensitive-data-safe logging.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring Boot Security Guidelines

## Role

You are a Senior software engineer with extensive experience in Spring Security and secure API design

## Goal

Apply secure-by-default Spring Boot practices: enforce authentication and authorization consistently, define least-privilege access control, use stateless or session-based models deliberately, and avoid sensitive data exposure in logs and API responses. Align with `@302-frameworks-spring-boot-rest` for error bodies on 401/403.

## Constraints

Before applying recommendations, ensure the project compiles. Compilation failure is a blocking condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: SecurityFilterChain and request authorization
- Example 2: Method-level security
- Example 3: Stateless APIs: CSRF and session
- Example 4: CORS configuration
- Example 5: OAuth2 Resource Server (JWT)
- Example 6: Password encoding and user store
- Example 7: Authentication and access-denied handling
- Example 8: Security headers

### Example 1: SecurityFilterChain and request authorization

Title: Explicit matchers; default deny for protected APIs
Description: Order matchers from most specific to general. Use `permitAll` only for health, public docs, and true anonymous endpoints. Prefer `authenticated()` as the default for API surfaces unless the product is intentionally public.

**Good example:**

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class ApiSecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(registry -> registry
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .build();
    }
}
```

**Bad example:**

```java
http.authorizeHttpRequests(r -> r.anyRequest().permitAll());
// Every endpoint is public
```

### Example 2: Method-level security

Title: @EnableMethodSecurity and @PreAuthorize
Description: Protect service-layer entry points when URLs alone are insufficient (e.g. multi-tenant resources, ownership checks). Use SpEL expressions that reference method arguments or `@authenticationPrincipal`.

**Good example:**

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

@Configuration
@EnableMethodSecurity
class MethodSecurityConfiguration { }

@Service
class OrderService {
    @PreAuthorize("hasRole('ADMIN') or @orderAuth.ownsOrder(#orderId, authentication.name)")
    void cancel(long orderId) { }
}
```

**Bad example:**

```java
@Service
class BadOrderService {
    void cancel(long orderId, String callerToken) {
        if (!"secret".equals(callerToken)) {
            throw new SecurityException("no");
        }
    }
}
```

### Example 3: Stateless APIs: CSRF and session

Title: Disable CSRF only when using token-based auth without session cookies
Description: For pure REST APIs authenticated via `Authorization: Bearer` without browser cookie sessions, CSRF protection on Spring Security’s session model is often disabled **together with** a stateless session policy. If the app uses session cookies for authentication, keep CSRF enabled or use a proper token strategy.

**Good example:**

```java
import org.springframework.security.config.http.SessionCreationPolicy;
// Inside SecurityFilterChain configuration for a JWT-only API:
http
    .csrf(csrf -> csrf.disable())
    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```

**Bad example:**

```java
http.csrf(csrf -> csrf.disable());
// Disabled CSRF while still using session cookie login from a browser — vulnerable to CSRF
```

### Example 4: CORS configuration

Title: Whitelist origins; avoid * with credentials
Description: Restrict `allowedOriginPatterns` or `allowedOrigins` to known front-end hosts. Using `*` with `allowCredentials(true)` is invalid in browsers and unsafe if misconfigured.

**Good example:**

```java
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

CorsConfigurationSource cors() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOriginPatterns(java.util.List.of("https://app.example.com"));
    c.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE"));
    c.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
    c.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", c);
    return source;
}
```

**Bad example:**

```java
cors.addAllowedOriginPattern("*");
cors.setAllowCredentials(true);
// Browser rejection or accidental over-permissive cross-origin access
```

### Example 5: OAuth2 Resource Server (JWT)

Title: Validate bearer tokens with issuer URI; wire the SecurityFilterChain
Description: For APIs behind an identity provider, use `spring-security-oauth2-resource-server` with `oauth2ResourceServer().jwt(...)`. Configure issuer URI in properties so Spring Boot auto-fetches the JWK set; wire the security filter chain to use it. Never embed symmetric secrets in source code.

**Good example:**

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://idp.example.com/realms/myrealm
```

**Bad example:**

```java
String HARDCODED_HS256_SECRET = "change-me";
// Never ship shared secrets in source for token validation in production
```

### Example 6: Password encoding and user store

Title: DelegatingPasswordEncoder for upgradeable credential hashes
Description: Never store plaintext passwords. Prefer `PasswordEncoderFactories.createDelegatingPasswordEncoder()` as the default because it stores `{id}` prefixes (for example `{bcrypt}`) and supports future password-hash upgrades without rewriting all credentials.

**Good example:**

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class CryptoConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```

**Bad example:**

```java
user.setPassword(plainTextPassword);
userRepository.save(user);
// Plaintext password persisted
```

### Example 7: Authentication and access-denied handling

Title: Consistent 401/403 JSON responses without stack traces
Description: Customize both `AuthenticationEntryPoint` (unauthenticated → 401) and `AccessDeniedHandler` (authenticated but forbidden → 403) for APIs so clients receive JSON or Problem Details instead of HTML error pages.

**Good example:**

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

class Json401EntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         org.springframework.security.core.AuthenticationException ex) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"UNAUTHORIZED\"}");
    }
}

class Json403Handler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res,
                       AccessDeniedException ex) throws IOException {
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"FORBIDDEN\"}");
    }
}
```

**Bad example:**

```java
// Default behaviour: HTML 401/403 pages returned to API clients expecting JSON
```

### Example 8: Security headers

Title: Frame options, content type options, HSTS where HTTPS
Description: Enable Spring Security’s default security headers for browser-facing apps. For pure machine-to-machine JSON APIs, some headers matter less but do not weaken TLS or mixed-content assumptions.

**Good example:**

```java
import org.springframework.security.config.Customizer;

http.headers(Customizer.withDefaults());
// Spring Security enables X-Content-Type-Options, cache control for authenticated resources, etc.

http.headers(h -> h.frameOptions(frame -> frame.deny()));
// Add explicit frame denial when serving browser-facing HTML admin alongside the API
```

**Bad example:**

```java
http.headers(h -> h.frameOptions(f -> f.sameOrigin()));
// Misapplied on an API that should deny framing entirely for admin UIs
```


## Output Format

- **ANALYZE** SecurityFilterChain rules, method security, CSRF/session model, CORS, authentication mechanism, and error handling for 401/403
- **CATEGORIZE** issues: misconfigured `permitAll`, missing authorization on admin routes, unsafe CSRF disable, overly broad CORS, weak password storage, information disclosure in errors
- **APPLY** least-privilege matchers, correct stateless/session strategy, strong password encoding, and JSON/Problem Details for security failures
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes; add integration tests for forbidden and unauthenticated access


## Safeguards

- Do not weaken endpoint protection to green-light failing tests
- Do not log tokens, passwords, or full Authorization headers
- Document threat model when disabling CSRF or using wildcard CORS