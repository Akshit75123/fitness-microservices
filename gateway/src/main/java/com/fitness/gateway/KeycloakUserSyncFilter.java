package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("hello!");
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        RegisterRequest registerRequest = extractUserFromToken(authHeader);
        log.info(registerRequest.toString());

        if (registerRequest == null || registerRequest.getKeycloakId() == null) {
            return unauthorized(exchange);
        }

        String keycloakId = registerRequest.getKeycloakId();

        // Validate user by Keycloak ID only
        return userService.validateUser(keycloakId)
                .flatMap(exists -> {

                    //  Register user if not present
                    if (!exists) {
                        log.info("Registering new user with Keycloak ID: {}", keycloakId);
                        return userService.registerUser(registerRequest);
                    }

                    return Mono.empty();
                })
                .then(Mono.defer(() -> {

                    // Propagate trusted user ID downstream
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-ID", keycloakId)
                            .build();

                    return chain.filter(
                            exchange.mutate()
                                    .request(mutatedRequest)
                                    .build()
                    );
                }));
    }

    private RegisterRequest extractUserFromToken(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();

            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setKeycloakId(claims.getStringClaim("sub"));
            request.setEmail(claims.getStringClaim("email"));
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));
            request.setPassword("dummy123123");

            return request;

        } catch (Exception e) {
            log.error("Failed to parse Keycloak token", e);
            return null;
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
