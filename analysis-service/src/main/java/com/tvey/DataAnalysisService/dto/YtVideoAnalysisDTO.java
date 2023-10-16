package com.tvey.DataAnalysisService.dto;

import com.tvey.DataAnalysisService.entity.CommentAnalysisResult;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class YtVideoAnalysisDTO extends AnalysisDTO {

    private VideoAnalysisResult videoAnalysisResult;

    private List<CommentAnalysisResult> commentAnalysisResults;

    public YtVideoAnalysisDTO(DataSource dataSource, VideoAnalysisResult videoAnalysisResult, List<CommentAnalysisResult> commentAnalysisResults) {
        super(dataSource);
        this.videoAnalysisResult = videoAnalysisResult;
        this.commentAnalysisResults = commentAnalysisResults;
    }
}
