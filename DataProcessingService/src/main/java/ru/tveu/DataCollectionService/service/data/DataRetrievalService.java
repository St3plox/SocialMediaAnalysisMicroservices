package ru.tveu.DataCollectionService.service.data;

import ru.tveu.DataCollectionService.dto.ApiTransferObject;

import java.io.IOException;
import java.util.List;

public interface DataRetrievalService {

    List<? extends ApiTransferObject> retrieveData(String url) throws IOException;

}