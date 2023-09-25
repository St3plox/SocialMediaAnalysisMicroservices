package ru.tveu.DataCollectionService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.tveu.DataCollectionService.dto.ApiTransferObject;
import ru.tveu.DataCollectionService.service.data.DataRetrievalService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataCollectionController {

    private final DataRetrievalService dataRetrievalService;

    @GetMapping("/yt")
    @ResponseStatus(HttpStatus.OK)
    public List<? extends ApiTransferObject> getVideoComments(@RequestParam String url) throws IOException {
        return dataRetrievalService.retrieveData(url);
    }


}