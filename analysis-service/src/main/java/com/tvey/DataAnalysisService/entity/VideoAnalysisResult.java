package com.tvey.DataAnalysisService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "video_analysis_result")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class VideoAnalysisResult extends AbstractAnalysisResult {


    @Column(name = "video_id", unique = true)
    private String videoId;

    @Column(name = "positive_amount")
    private int positive;

    @Column(name = "negative_amount")
    private int negative;


    @Column(name = "neutral_amount")
    private int neutral;


    @Column(name = "irrelevant_amount")
    private int irrelevant;

}