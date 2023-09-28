package com.tvey.DataAnalysisService.controller;

import com.tvey.DataAnalysisService.entity.CommentSentiment;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import com.tvey.DataAnalysisService.service.TextAnalyzer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final TextAnalyzer textAnalyzer;

    @GetMapping("/yt")
    @ResponseStatus(HttpStatus.OK)
    public VideoAnalysisResult getVideoComments(@RequestParam String url, @RequestParam(defaultValue = "5000") long maxComments) throws IOException, ClassNotFoundException {
        return textAnalyzer.analyzeEntity(url,  maxComments);
    }
}
