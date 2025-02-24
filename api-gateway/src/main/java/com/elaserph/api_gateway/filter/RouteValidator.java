package com.elaserph.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> nonJwtSecuredEndpoints = List.of(
            "/auth/register", "/auth/authenticate", "/auth/logout", "/oauth2/authorization/google"  // public facing
            , "/auth/validate", "/auth/refresh", "/auth/payload", "/h2-console/**", "/eureka" // private
    );

    public static final List<String> privateAuthEndpoints = List.of(
            "/auth/refresh", "/auth/validate", "/auth/payload", "/h2-console/**", "/eureka"
    );

    public Predicate<ServerHttpRequest> isJwtSecuredEndpoint =
            request -> nonJwtSecuredEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isPrivateAuthEndpoint =
            request -> privateAuthEndpoints.stream()
                    .anyMatch(uri -> request.getURI().getPath().contains(uri));

}
