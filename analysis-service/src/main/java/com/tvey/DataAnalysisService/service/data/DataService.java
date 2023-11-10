package com.tvey.DataAnalysisService.service.data;

import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.ContentDTO;

import java.util.Set;

public interface DataService<T extends ContentDTO>{

    ApiDTO<T> retrieveData(String url, long maxResults, Set<String> usedIds);
}
