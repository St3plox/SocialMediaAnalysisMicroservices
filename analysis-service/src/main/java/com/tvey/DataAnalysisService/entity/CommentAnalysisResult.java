package com.tvey.DataAnalysisService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentAnalysisResult {

    private String id;

    private String videoUrl;

    private String ownerName;

    private String content;

    private String date;

    private String analysisResult;


}