package com.tvey.DataAnalysisService.controller;

import com.tvey.DataAnalysisService.dto.YtVideoAnalysisDTO;
import com.tvey.DataAnalysisService.service.result.interfaces.VideoAnalysisResultService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final VideoAnalysisResultService videoAnalysisResult;

    @GetMapping("/yt")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "data-collection", fallbackMethod = "fallbackMethod")
    public YtVideoAnalysisDTO getVideoComments(@RequestParam String url,
                                               @RequestParam(defaultValue = "5000") long maxComments) {

        return videoAnalysisResult.getVideoAnalysisResult(url, maxComments);
    }


    public YtVideoAnalysisDTO fallbackMethod(String url, long maxComments, RuntimeException runtimeException) {
        throw runtimeException;
    }
}
