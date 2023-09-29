package com.tvey.DataAnalysisService.service;

import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;

import java.io.IOException;

public interface TextAnalyzer {

    VideoAnalysisResult analyzeEntity(String url, long maxResponse) throws IOException, ClassNotFoundException;
}