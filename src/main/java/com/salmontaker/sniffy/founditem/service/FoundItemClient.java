package com.salmontaker.sniffy.founditem.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salmontaker.sniffy.common.OpenApiResponse;
import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class FoundItemClient {
    private final WebClient webClient;

    private final String baseURL = "https://apis.data.go.kr/1320000";
    private final String endpoint = "/LosfundInfoInqireService/getLosfundInfoAccToClAreaPd";

    @Value("${data.go.kr.api.key}")
    private String serviceKey;

    public FoundItemClient() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
                    configurer.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
                    configurer.defaultCodecs()
                            .maxInMemorySize(20 * 1024 * 1024); // 20 MB
                })
                .build();

        this.webClient = WebClient.builder()
                .uriBuilderFactory(new DefaultUriBuilderFactory(baseURL) {{
                    setEncodingMode(EncodingMode.NONE); // Encoding 키를 사용하므로 NONE
                }})
                .exchangeStrategies(strategies)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<OpenApiResponse<LostFoundResponse>> fetchData(String startDate, String endDate, int pageNo, int numOfRows) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(endpoint)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("START_YMD", startDate)
                        .queryParam("END_YMD", endDate)
                        .queryParam("pageNo", pageNo)
                        .queryParam("numOfRows", numOfRows)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}
