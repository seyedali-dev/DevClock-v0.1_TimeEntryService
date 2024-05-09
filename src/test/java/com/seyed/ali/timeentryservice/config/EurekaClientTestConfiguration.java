package com.seyed.ali.timeentryservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;

/**
 * A configuration class that provides a Bean for the ServiceInstanceListSupplier interface.
 * During testing, Spring's dependency injection will use this bean instead of the actual ServiceInstanceListSupplier.
 * This allows the application to be tested without needing a running Eureka server.
 * <p>
 * <br>
 * During testing, we don't want to run a full Eureka server; otherwise we'll get into error while trying to build the project {@code mvn clean install}.
 * <p>
 * This class allows you to simulate the behavior of Eureka without needing to run the actual server.
 * It returns a static list of service instances, which your application can use as if it was the actual list from Eureka.
 */
@TestConfiguration
public class EurekaClientTestConfiguration {

    /**
     * Returns a ServiceInstanceListSupplier that provides a list of service instances for a service ID.
     * In this case, it returns an instance of the TestServiceInstanceListSupplier class.
     * This allows the application to be tested as if it was interacting with a real Eureka server.
     *
     * @return a ServiceInstanceListSupplier
     */
    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new TestServiceInstanceListSupplier();
    }

}
