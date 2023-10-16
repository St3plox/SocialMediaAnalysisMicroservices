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
import org.springframework.stereotype.Service;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class VideoAnalysisResultServiceImpl extends AnalysisResultServiceImpl<VideoAnalysisResult> implements VideoAnalysisResultService {

    private final VideoAnalyzer videoAnalyzer;

    private final YtDataService dataService;

    private final VideoAnalysisResultRepository videoAnalysisResultRepository;

    private final CommentAnalysisResultRepository commentAnalysisResultRepository;


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

        if (optionalAnalysisResult.isEmpty())
            throw new IllegalArgumentException("No such video analysis result with id " + id);

        return retrieveYtAnalysisDto(optionalAnalysisResult.get());
    }

    @Override
    public YtVideoAnalysisDTO getVideoAnalysisResult(String videoId) {

        Optional<VideoAnalysisResult> optionalAnalysisResult = videoAnalysisResultRepository.findByVideoId(videoId);

        if (optionalAnalysisResult.isEmpty())
            throw new IllegalArgumentException("No such video analysis result with id " + videoId);


        return retrieveYtAnalysisDto(optionalAnalysisResult.get());
    }

    @Override
    public YtVideoAnalysisDTO getVideoAnalysisResult(String url, long maxComments) {

        String videoId = dataService.extractVideoId(url);
        Optional<VideoAnalysisResult> resultOptional = videoAnalysisResultRepository.findByVideoId(videoId);

        if (resultOptional.isPresent()) {
            return retrieveYtAnalysisDto(resultOptional.get());
        }

        ApiDTO<YtContentDTO> apiDTO = dataService.retrieveData(url, maxComments);

        YtVideoAnalysisDTO dto;

        try {
            dto = videoAnalyzer.analyzeEntity(apiDTO);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        resultRepository.save(dto.getVideoAnalysisResult());
        commentAnalysisResultRepository.saveAll(dto.getCommentAnalysisResults());

        return dto;
    }

    private YtVideoAnalysisDTO retrieveYtAnalysisDto(VideoAnalysisResult analysisResult) {

        List<CommentAnalysisResult> commentAnalysisResults = commentAnalysisResultRepository
                .findCommentAnalysisResultByVideoAnalysisResult(analysisResult);

        return YtVideoAnalysisDTO.builder()
                .videoAnalysisResult(analysisResult)
                .commentAnalysisResults(commentAnalysisResults)
                .dataSource(DataSource.YOUTUBE)
                .build();
    }
}
