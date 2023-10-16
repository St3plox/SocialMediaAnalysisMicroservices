package ru.tveu.DataCollectionService.service.data;

import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;

public interface DataRetrievalService {

    ApiDTO<YtContentDTO> retrieveData(String url, long maxResponse) throws IOException;

}