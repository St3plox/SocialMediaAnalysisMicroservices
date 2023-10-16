package com.tvey.DataAnalysisService.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity(name = "analysis_result")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
public abstract class AbstractAnalysisResult {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "timestamp", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_date")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}