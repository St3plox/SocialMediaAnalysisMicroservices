package com.tvey.DataAnalysisService.controller;

import com.tvey.DataAnalysisService.dto.YtVideoAnalysisDTO;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import com.tvey.DataAnalysisService.service.result.interfaces.VideoAnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final VideoAnalysisResultService videoAnalysisResult;

    @GetMapping("/yt")
    @ResponseStatus(HttpStatus.OK)
    public YtVideoAnalysisDTO getVideoComments(@RequestParam String url, @RequestParam(defaultValue = "5000")
    long maxComments) {

        return videoAnalysisResult.getVideoAnalysisResult(url,  maxComments);
    }
}
