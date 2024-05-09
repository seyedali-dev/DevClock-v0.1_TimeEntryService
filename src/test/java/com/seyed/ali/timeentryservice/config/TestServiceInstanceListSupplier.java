package com.seyed.ali.timeentryservice.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * A mock implementation of the ServiceInstanceListSupplier interface.
 * This class is used during testing to simulate the behavior of a Eureka server.
 * It provides a static list of service instances, allowing the application to be tested without a running Eureka server.
 */
@Component
public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {

    /**
     * Returns the service ID for this supplier.
     * In this case, it returns null because this is a test implementation.
     *
     * @return null
     */
    @Override
    public String getServiceId() {
        return null;
    }

    /**
     * Returns a Flux that emits new service instance lists for the service ID.
     * In this case, it returns a Flux that emits a static list of service instances.
     * This allows the application to be tested as if it was interacting with a real Eureka server.
     *
     * @return a Flux that emits a list of service instances
     */
    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> defaultServiceInstanceList = List.of(
                new DefaultServiceInstance(
                        "Authentication-Service",
                        "Authentication-Service",
                        "localhost",
                        8081,
                        false
                ),
                new DefaultServiceInstance(
                        "Time-Entry-Service",
                        "Time-Entry-Service",
                        "localhost",
                        8082,
                        false
                )
        );
        return Flux.just(defaultServiceInstanceList);
    }

}
