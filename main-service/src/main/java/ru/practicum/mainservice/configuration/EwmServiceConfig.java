package ru.practicum.mainservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.practicum.client.StatClient;

@Configuration
@EnableScheduling
public class EwmServiceConfig {

    @Value("${stats-server.url}")
    private String serverUrl;

    @Bean
    public StatClient statClient() {
        return new StatClient(serverUrl, new RestTemplateBuilder());
    }
}