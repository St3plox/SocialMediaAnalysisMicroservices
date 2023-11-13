package com.tvey.DataAnalysisService.service.result.implemenation;

import com.tvey.DataAnalysisService.dto.DataSource;
import com.tvey.DataAnalysisService.dto.YtVideoAnalysisDTO;
import com.tvey.DataAnalysisService.entity.CommentAnalysisResult;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import com.tvey.DataAnalysisService.repository.AnalysisResultRepository;
import com.tvey.DataAnalysisService.repository.CommentAnalysisResultRepository;
import com.tvey.DataAnalysisService.repository.VideoAnalysisResultRepository;
import com.tvey.DataAnalysisService.service.analyzer.VideoAnalyzer;
import com.tvey.DataAnalysisService.service.data.YtDataService;
import com.tvey.DataAnalysisService.service.result.interfaces.VideoAnalysisResultService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;
import java.util.*;


// TODO: 30.10.2023 Fix already exists exception

@Service
@Slf4j
public class VideoAnalysisResultServiceImpl extends AnalysisResultServiceImpl<VideoAnalysisResult> implements VideoAnalysisResultService {

    private final VideoAnalyzer videoAnalyzer;

    private final YtDataService dataService;

    private final VideoAnalysisResultRepository videoAnalysisResultRepository;

    private final CommentAnalysisResultRepository commentAnalysisResultRepository;

    private final Logger logger = LoggerFactory.getLogger(VideoAnalysisResultServiceImpl.class);


    public VideoAnalysisResultServiceImpl(AnalysisResultRepository<VideoAnalysisResult> resultRepository,
                                          VideoAnalyzer videoAnalyzer,
                                          YtDataService dataService, VideoAnalysisResultRepository videoAnalysisResultRepository, VideoAnalysisResultRepository videoAnalysisResultRepository1, CommentAnalysisResultRepository commentAnalysisResultRepository) {
        super(resultRepository);

        this.videoAnalyzer = videoAnalyzer;
        this.dataService = dataService;
        this.videoAnalysisResultRepository = videoAnalysisResultRepository1;
        this.commentAnalysisResultRepository = commentAnalysisResultRepository;
    }

    @Override
    public YtVideoAnalysisDTO getVideoAnalysisResult(Long id) {
        Optional<VideoAnalysisResult> optionalAnalysisResult = videoAnalysisResultRepository.findById(id);
        return retrieveYtAnalysisDto(optionalAnalysisResult);
    }

    @Override
    public YtVideoAnalysisDTO getVideoAnalysisResult(String videoId) {
        Optional<VideoAnalysisResult> optionalAnalysisResult = videoAnalysisResultRepository.findByVideoId(videoId);
        return retrieveYtAnalysisDto(optionalAnalysisResult);
    }

    @Override
    public YtVideoAnalysisDTO getVideoAnalysisResult(String url, long maxComments) {

        String videoId = dataService.extractVideoId(url);
        Optional<VideoAnalysisResult> resultOptional = videoAnalysisResultRepository.findByVideoId(videoId);

        if (resultOptional.isPresent()) {
            return retrieveYtAnalysisDto(resultOptional, maxComments, url);
        }

        ApiDTO<YtContentDTO> apiDTO = dataService.retrieveData(url, maxComments, new HashSet<>(0));
        YtVideoAnalysisDTO resultDTO = analyzeResults(apiDTO);


        // Save the VideoAnalysisResult
        videoAnalysisResultRepository.save(resultDTO.getVideoAnalysisResult());

        // Update references in CommentAnalysisResult entities
        resultDTO.getCommentAnalysisResults().forEach(comment -> comment.setVideoAnalysisResult(resultDTO.getVideoAnalysisResult()));

        // Save the CommentAnalysisResult entities
        commentAnalysisResultRepository.saveAll(resultDTO.getCommentAnalysisResults());


        return resultDTO;
    }

    private YtVideoAnalysisDTO analyzeResults(ApiDTO<YtContentDTO> apiDTO) {
        YtVideoAnalysisDTO dto;

        try {
            dto = videoAnalyzer.analyzeEntity(apiDTO);
        } catch (IOException e) {
            logger.error("An error occurred in VideoAnalysisResultServiceImpl:", e);
            throw new RuntimeException(e);
        }
        return dto;
    }

    private YtVideoAnalysisDTO retrieveYtAnalysisDto(Optional<VideoAnalysisResult> optionalAnalysisResult) {

        VideoAnalysisResult analysisResult = optionalAnalysisResult.orElseThrow(() ->
                new IllegalArgumentException("No such video analysis result"));

        List<CommentAnalysisResult> commentAnalysisResults = commentAnalysisResultRepository
                .findAllByVideoAnalysisResultOrderByPublishedAtDesc(
                        analysisResult,
                        PageRequest.of(0,
                                5000)
                );

        return YtVideoAnalysisDTO.builder()
                .videoAnalysisResult(analysisResult)
                .commentAnalysisResults(commentAnalysisResults)
                .dataSource(DataSource.YOUTUBE)
                .build();
    }

    private YtVideoAnalysisDTO retrieveYtAnalysisDto(Optional<VideoAnalysisResult> optionalAnalysisResult, long maxComments, String url) {

        VideoAnalysisResult analysisResult = optionalAnalysisResult.orElseThrow(() ->
                new IllegalArgumentException("No such video analysis result"));

        List<CommentAnalysisResult> commentAnalysisResults = commentAnalysisResultRepository
                .findAllByVideoAnalysisResultOrderByPublishedAtDesc(
                        analysisResult,
                        PageRequest.of(0,
                                (int) maxComments)
                );

        if (commentAnalysisResults.size() < maxComments) {

            Set<String> usedCommentIds = new HashSet<>();
            commentAnalysisResults.forEach(comment -> usedCommentIds.add(comment.getCommentId()));

            ApiDTO<YtContentDTO> additionalCommentsDTO =
                    dataService.retrieveData(
                            url,
                            maxComments,
                            usedCommentIds
                    );

            List<CommentAnalysisResult> additionalComments = analyzeResults(additionalCommentsDTO).getCommentAnalysisResults();
            List<CommentAnalysisResult> duplicatesToRemove = new ArrayList<>();

            for (var commentAR : commentAnalysisResults) {
                for (var addCommentAR : additionalComments) {
                    if (Objects.equals(commentAR.getCommentId(), addCommentAR.getCommentId())) {
                        duplicatesToRemove.add(addCommentAR);
                        System.out.println("FOUND DUPLICATE VALUE !!!!!!!!!!" +
                                "\n !!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                                "\n !!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                                "\n !!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                                "\n !!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                }
            }

            additionalComments.removeAll(duplicatesToRemove);

            commentAnalysisResults.addAll(additionalComments);

            int positive = 0;
            int negative = 0;
            int neutral = 0;
            int irrelevant = 0;

            for (var comment : additionalComments) {
                switch (comment.getCategory()) {
                    case "Positive" -> positive++;
                    case "Negative" -> negative++;
                    case "Neutral" -> neutral++;
                    case "Irrelevant" -> irrelevant++;
                }
            }

            analysisResult.setPositive(analysisResult.getPositive() + positive);
            analysisResult.setNegative(analysisResult.getNegative() + negative);
            analysisResult.setNeutral(analysisResult.getNeutral() + neutral);
            analysisResult.setIrrelevant(analysisResult.getIrrelevant() + irrelevant);



            videoAnalysisResultRepository.save(analysisResult);
        }

        return YtVideoAnalysisDTO.builder()
                .videoAnalysisResult(analysisResult)
                .commentAnalysisResults(commentAnalysisResults)
                .dataSource(DataSource.YOUTUBE)
                .build();
    }
}
