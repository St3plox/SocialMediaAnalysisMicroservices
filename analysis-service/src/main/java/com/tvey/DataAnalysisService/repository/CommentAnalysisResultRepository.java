package com.tvey.DataAnalysisService.repository;

import com.tvey.DataAnalysisService.entity.CommentAnalysisResult;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentAnalysisResultRepository extends AnalysisResultRepository<CommentAnalysisResult> {

    List<CommentAnalysisResult> findCommentAnalysisResultByVideoAnalysisResult(VideoAnalysisResult videoAnalysisResult);
}
