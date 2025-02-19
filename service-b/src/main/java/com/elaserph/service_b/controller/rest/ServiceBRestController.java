package com.elaserph.service_b.controller.rest;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class ServiceBRestController {

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    public ServiceBRestController(DiscoveryClient discoveryClient, RestClient.Builder restClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.restClient = restClientBuilder.build();
    }

    @GetMapping("helloEureka")
    public String helloWorld() {
        ServiceInstance serviceInstance = discoveryClient.getInstances("service-a").get(0);
        return restClient.get()
                .uri(serviceInstance.getUri() + "/helloWorld")
                .retrieve()
                .body(String.class);
    }
}
