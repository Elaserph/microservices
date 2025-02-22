package com.elaserph.api_gateway.filter;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final RouteValidator routeValidator;
    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;

    public static class Config {


    }
    public AuthFilter(RouteValidator routeValidator, DiscoveryClient discoveryClient,
                      WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.discoveryClient = discoveryClient;
        this.webClient = webClientBuilder.build();
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(AuthFilter.Config config) {
        return ((exchange, chain) -> {
            if(routeValidator.isSecured.test(exchange.getRequest()) &&
                    !exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("Missing Authorization Header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer "))
                    authHeader = authHeader.substring(7);

            ServiceInstance authServiceInstance = discoveryClient.getInstances("auth-service").get(0);

            if(routeValidator.isNotRefresh.test(exchange.getRequest())){
                try {
                    var valid = webClient.get()
                            .uri(authServiceInstance.getUri() + "/validate?token" + authHeader)
                            .retrieve().bodyToMono(Boolean.class).block();
                    if(Boolean.FALSE.equals(valid))
                        throw new RuntimeException("Token Expired!");
                } catch (Exception e) {
                    throw new RuntimeException("InValid Token!");
                }
            } else {
                try {
                    var accessToken = webClient.get()
                            .uri(authServiceInstance.getUri() + "/refresh?token" + authHeader)
                            .retrieve().bodyToMono(String.class).block();
                    System.out.println("AccessToken: "+accessToken);
                } catch (Exception e) {
                    throw new RuntimeException("Something went wrong!");
                }
            }
            return chain.filter(exchange);
        });
    }

}
