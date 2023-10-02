package com.tvey.DataAnalysisService.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class YtTransferObject extends ApiTransferObject {

    private String authorDisplayName;

    private String publishedAt;

    private String videoId;

    public YtTransferObject(String id, DataSource source, String content,
                            String authorDisplayName, String publishedAt, String videoId) {
        super(id, source, content);
        this.authorDisplayName = authorDisplayName;
        this.publishedAt = publishedAt;
        this.videoId = videoId;
    }

}