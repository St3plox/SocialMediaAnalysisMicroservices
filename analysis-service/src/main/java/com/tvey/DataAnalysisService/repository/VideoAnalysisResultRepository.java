package com.tvey.DataAnalysisService.repository;

import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoAnalysisResultRepository extends AnalysisResultRepository<VideoAnalysisResult> {

    Optional<VideoAnalysisResult> findByVideoId(String videoId);

    boolean existsByVideoId(String videoId);

}