package com.tvey.DataAnalysisService.service;

import com.tvey.DataAnalysisService.dto.ApiTransferObject;
import com.tvey.DataAnalysisService.entity.CommentSentiment;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;

import java.io.IOException;
import java.util.List;

public interface TextAnalyzer {

    VideoAnalysisResult analyzeEntity(String url, long maxResponse) throws IOException, ClassNotFoundException;
}