package ru.tveu.DataCollectionService.service.data;

import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;
import java.util.Set;

public interface DataRetrievalService {

    ApiDTO<YtContentDTO> retrieveData(String url, long maxResponse, Set<String> usedIds) throws IOException;

}