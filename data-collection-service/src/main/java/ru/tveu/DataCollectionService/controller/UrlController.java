package ru.tveu.DataCollectionService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tveu.DataCollectionService.service.url.UrlProcessor;

@RestController
@RequestMapping("api/data/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlProcessor urlProcessor;

    @GetMapping("/yt")
    public ResponseEntity<String> processYtUrl(@RequestParam String url) {
        return new ResponseEntity<>(urlProcessor.extractContentId(url), HttpStatus.OK);
    }
}
