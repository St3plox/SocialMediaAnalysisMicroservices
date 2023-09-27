package com.tvey.DataAnalysisService.service;

import com.tvey.DataAnalysisService.dto.ApiTransferObject;

import java.util.List;

public interface DataProcessor {

    List<? extends ApiTransferObject> tokenizeComments(String url, long maxResponse);
}