package com.tvey.DataAnalysisService.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class YtDataService implements DataService<YtContentDTO> {

    private final WebClient.Builder webClient;


    @Override
    public ApiDTO<YtContentDTO> retrieveData(String url, long maxResults, Set<String> usedIds) {
        ParameterizedTypeReference<ApiDTO<YtContentDTO>> typeReference =
                new ParameterizedTypeReference<>() {};

        String usedIdsParam = usedIds.isEmpty() ? "" : "&usedIds=" + String.join(",", usedIds.toString());

        return webClient
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build()
                .get()
                .uri("http://data-processing-service/api/data/" +
                        "yt?url=" + url + "&" + "maxComments=" + maxResults + usedIdsParam)
                .retrieve()
                .bodyToMono(typeReference)
                .block();
    }

    public String extractVideoId(String url) {
        return webClient.codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build()
                .get()
                .uri("http://data-processing-service/api/data/url/yt" + "?url=" + url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
