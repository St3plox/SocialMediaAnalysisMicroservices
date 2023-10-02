package com.tvey.DataAnalysisService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoAnalysisResult {

    private long id;

    private String videoId;

    private double positivePart;

    private double negativePart;

    private double neutralPart;

    private double irrelevantPart;

    private int commentsAmount;

    private List<CommentAnalysisResult> commentAnalysisResults;
}