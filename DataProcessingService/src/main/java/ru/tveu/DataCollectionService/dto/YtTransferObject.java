package ru.tveu.DataCollectionService.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YtTransferObject extends ApiTransferObject {

    private String authorDisplayName;

    private String publishedAt;

    public YtTransferObject(String id, DataSource dataSource, String content, String authorDisplayName, String publishedAt) {
        super(id, dataSource, content);
        this.authorDisplayName = authorDisplayName;
        this.publishedAt = publishedAt;
    }

    public YtTransferObject() {
    }
}