package com.tvey.DataAnalysisService.service;

import com.tvey.DataAnalysisService.dto.ApiTransferObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DataProcessorImpl implements DataProcessor {

    private final WebClient.Builder webClient;

    @Override
    public List<? extends ApiTransferObject> tokenizeComments(String url) {

        return webClient
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build()
                .get()
                .uri("http://data-processing-service/api/data/" +
                        "yt?url=" + url)
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }
}
