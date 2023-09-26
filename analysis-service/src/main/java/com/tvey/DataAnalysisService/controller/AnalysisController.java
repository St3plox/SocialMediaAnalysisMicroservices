package com.tvey.DataAnalysisService.controller;

import com.tvey.DataAnalysisService.dto.ApiTransferObject;
import com.tvey.DataAnalysisService.service.DataProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final DataProcessor dataProcessor;

    @GetMapping("/yt")
    @ResponseStatus(HttpStatus.OK)
    public List<? extends ApiTransferObject> getVideoComments(@RequestParam String url) {
        return dataProcessor.tokenizeComments(url);
    }
}
