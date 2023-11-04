package com.guerrilla.scorecontroller.it.config;

import org.junit.Rule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {
    private static final DockerImageName LOCALSTACK_IMAGE_NAME = DockerImageName.parse("localstack/localstack");
    @Rule
    @Bean
    public LocalStackContainer localStackContainer() {
        LocalStackContainer localStackContainer = new LocalStackContainer(LOCALSTACK_IMAGE_NAME)
                .withServices(DYNAMODB);

        localStackContainer.start();
        return localStackContainer;
    }
}
