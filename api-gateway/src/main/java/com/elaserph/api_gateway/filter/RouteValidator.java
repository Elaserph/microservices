package com.elaserph.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/register", "/auth/authenticate", "/eureka"
            //"/auth/refresh", "/auth/validate"
    );

    public static final List<String> refreshTokenEndpoint = List.of(
            "/auth/refresh"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isNotRefresh =
            request -> refreshTokenEndpoint
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
