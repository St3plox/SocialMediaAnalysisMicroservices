package com.tvey.DataAnalysisService.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ApiTransferObject {

    private String id;

    private DataSource source;

    private String content;

}