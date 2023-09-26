package com.tvey.DataAnalysisService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiTransferObject {

    private String id;

    private DataSource source;

    private String content;

}