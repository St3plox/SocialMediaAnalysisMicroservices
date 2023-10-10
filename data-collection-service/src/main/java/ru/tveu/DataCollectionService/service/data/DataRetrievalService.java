package ru.tveu.DataCollectionService.service.data;

import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.ContentDTO;

import java.io.IOException;

public interface DataRetrievalService {

    ApiDTO<? extends ContentDTO> retrieveData(String url, long maxResponse) throws IOException;

}