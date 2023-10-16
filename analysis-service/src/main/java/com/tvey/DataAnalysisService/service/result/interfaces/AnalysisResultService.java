package com.tvey.DataAnalysisService.service.result.interfaces;

import com.tvey.DataAnalysisService.entity.AbstractAnalysisResult;

import java.util.List;

public interface AnalysisResultService<T extends  AbstractAnalysisResult>{

    void saveAnalysisResult(T result);

    void saveAnalysisResult(List<T> results);

}
