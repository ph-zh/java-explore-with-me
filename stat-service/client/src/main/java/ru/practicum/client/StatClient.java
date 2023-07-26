package ru.practicum.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.dto.StatResponseDto;

import java.util.List;
import java.util.Map;

public class StatClient {
    private final RestTemplate restTemplate;

    public StatClient(String serverUrl, RestTemplateBuilder builder) {
        restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addHit(EndpointHitRequestDto requestDto) {
        restTemplate.exchange("/hit", HttpMethod.POST, new HttpEntity<>(requestDto), Void.class);
    }

    public List<StatResponseDto> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> params = Map.of(
                "start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique
        );

        return restTemplate.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<StatResponseDto>>() {
                }, params).getBody();
    }
}