package com.tvey.DataAnalysisService.service.analyzer;

import com.tvey.DataAnalysisService.dto.AnalysisDTO;
import com.tvey.DataAnalysisService.entity.AbstractAnalysisResult;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.ContentDTO;

import java.io.IOException;

public interface TextAnalyzer<T extends ContentDTO, R extends AnalysisDTO>{

     R analyzeEntity(ApiDTO<T> apiDTO) throws IOException, ClassNotFoundException;
}