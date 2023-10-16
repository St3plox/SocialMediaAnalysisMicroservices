package com.tvey.DataAnalysisService.service.result.interfaces;

import com.tvey.DataAnalysisService.dto.YtVideoAnalysisDTO;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;

public interface VideoAnalysisResultService extends AnalysisResultService<VideoAnalysisResult>{

    YtVideoAnalysisDTO getVideoAnalysisResult(Long id);

    YtVideoAnalysisDTO getVideoAnalysisResult(String videoId);

    YtVideoAnalysisDTO getVideoAnalysisResult(String url, long maxComments);

}
