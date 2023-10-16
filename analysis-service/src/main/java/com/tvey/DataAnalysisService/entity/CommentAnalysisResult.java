
package com.tvey.DataAnalysisService.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity(name = "comment_analysis_result")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CommentAnalysisResult extends AbstractAnalysisResult{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "video_id", referencedColumnName = "video_id")
    private VideoAnalysisResult videoAnalysisResult;

    @Column(length = 1000)
    private String content;

    @Column(name = "publication_date")
    private Date publishedAt;

    @Column(name = "category")
    private String category;

}
