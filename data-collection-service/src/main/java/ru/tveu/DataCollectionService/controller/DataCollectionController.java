package ru.tveu.DataCollectionService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tveu.DataCollectionService.service.data.DataRetrievalService;
import ru.tveu.DataCollectionService.service.url.UrlProcessor;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataCollectionController {

    private final DataRetrievalService dataRetrievalService;

    private final UrlProcessor urlProcessor;

    @GetMapping(value = "/yt")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDTO<YtContentDTO>> getVideoCommentsByUrl(@RequestParam String url, @RequestParam long maxComments)
            throws IOException {

        String videoId = urlProcessor.extractContentId(url);

        ApiDTO<YtContentDTO> response = dataRetrievalService.retrieveData(videoId, maxComments);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/yt/id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDTO<YtContentDTO>> getVideoCommentsById(@RequestParam String videoId, @RequestParam long maxComments)
            throws IOException {

        ApiDTO<YtContentDTO> response = dataRetrievalService.retrieveData(videoId, maxComments);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}