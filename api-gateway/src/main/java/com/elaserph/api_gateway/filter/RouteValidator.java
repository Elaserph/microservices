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

    public static final List<String> tokenEndpoint = List.of(
            "/auth/refresh", "/auth/validate"
    );

    public Predicate<ServerHttpRequest> isSecuredEndpoint =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isNotTokenEndpoint =
            request -> tokenEndpoint
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
