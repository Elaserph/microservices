package com.elaserph.api_gateway.filter;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final RouteValidator routeValidator;
    private final WebClient webClient;

    public AuthFilter(RouteValidator routeValidator, DiscoveryClient discoveryClient,
                      WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClient = webClientBuilder.build();
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(AuthFilter.Config config) {
        return ((exchange, chain) -> {
            if(routeValidator.isSecuredEndpoint.test(exchange.getRequest()) &&
                    !exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("Missing Authorization Header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer "))
                    authHeader = authHeader.substring(7);

            if(routeValidator.isNotTokenEndpoint.test(exchange.getRequest())){
                Mono<Boolean> validToken= webClient
                        .get()
                        .uri("lb://AUTH-SERVICE/auth/validate?token=" + authHeader)
                        .retrieve()
                        .bodyToMono(Boolean.class);

                return validToken.flatMap(valid -> {
                            if (Boolean.TRUE.equals(valid)) {
                                return chain.filter(exchange);
                            } else {
                                return onError(exchange, "Invalid Token!", HttpStatus.UNAUTHORIZED);
                            }
                        })
                        .onErrorResume(e -> onError(exchange, "Token validation error: " + e.getMessage(), HttpStatus.UNAUTHORIZED));
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuration properties (if any)
    }

}
