package com.tvey.DataAnalysisService.service.data;

import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.ContentDTO;

public interface DataService<T extends ContentDTO>{

    ApiDTO<T> retrieveData(String url, long maxResults);
}
