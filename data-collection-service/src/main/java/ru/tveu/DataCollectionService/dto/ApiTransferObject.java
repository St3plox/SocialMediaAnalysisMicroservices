package ru.tveu.DataCollectionService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@SuperBuilder
public class ApiTransferObject {

    private String id;

    private DataSource source;

    private String content;

}