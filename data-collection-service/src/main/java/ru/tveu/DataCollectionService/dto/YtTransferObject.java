package ru.tveu.DataCollectionService.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class YtTransferObject extends ApiTransferObject {

    private String authorDisplayName;

    private String publishedAt;

    public YtTransferObject(String id, DataSource source, String content, String authorDisplayName, String publishedAt) {
        super(id, source, content);
        this.authorDisplayName = authorDisplayName;
        this.publishedAt = publishedAt;
    }
}