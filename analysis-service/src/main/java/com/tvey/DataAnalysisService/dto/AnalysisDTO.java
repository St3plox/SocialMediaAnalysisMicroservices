package com.tvey.DataAnalysisService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public abstract class AnalysisDTO {

    DataSource dataSource;
}
