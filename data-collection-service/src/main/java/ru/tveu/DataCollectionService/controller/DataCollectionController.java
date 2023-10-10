package ru.tveu.DataCollectionService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tveu.DataCollectionService.service.data.DataRetrievalService;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataCollectionController {

    private final DataRetrievalService dataRetrievalService;

    @GetMapping(value = "/yt")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDTO<YtContentDTO>> getVideoComments(@RequestParam String url, @RequestParam long maxComments)
            throws IOException {

        ApiDTO<YtContentDTO> response = (ApiDTO<YtContentDTO>) dataRetrievalService.retrieveData(url, maxComments);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}