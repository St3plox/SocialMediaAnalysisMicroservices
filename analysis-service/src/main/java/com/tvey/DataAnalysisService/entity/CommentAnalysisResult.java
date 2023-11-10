
package com.tvey.DataAnalysisService.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity(name = "comment_analysis_result")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CommentAnalysisResult extends AbstractAnalysisResult{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "video_id", referencedColumnName = "video_id")
    @JsonIgnore
    private VideoAnalysisResult videoAnalysisResult;

    @Column(length = 1000)
    private String content;

    @Column(length = 1000)
    private String commentId;

    @Column(name = "publication_date")
    private Date publishedAt;

    @Column(name = "category")
    private String category;

}
