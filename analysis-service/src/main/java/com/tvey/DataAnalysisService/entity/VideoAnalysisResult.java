package com.tvey.DataAnalysisService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoAnalysisResult {

    private String id;

    private String videUrl;

    private double positivePart;

    private double negativePart;

    private double neutralPart;

    private double irrelevantPart;

    private int commentsAmount;

    private List<CommentAnalysisResult> commentAnalysisResults;
}
