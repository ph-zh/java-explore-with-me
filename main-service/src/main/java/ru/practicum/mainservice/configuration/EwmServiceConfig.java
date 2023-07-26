package ru.practicum.mainservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.client.StatClient;

@Configuration
public class EwmServiceConfig {

    @Value("${stats-server.url}")
    private String serverUrl;

    @Bean
    public StatClient statClient() {
        return new StatClient(serverUrl, new RestTemplateBuilder());
    }
}
