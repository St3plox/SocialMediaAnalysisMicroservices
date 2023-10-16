package com.tvey.DataAnalysisService.service.analyzer;


import com.tvey.DataAnalysisService.dto.DataSource;
import com.tvey.DataAnalysisService.dto.YtVideoAnalysisDTO;
import com.tvey.DataAnalysisService.entity.CommentAnalysisResult;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import com.tvey.DataAnalysisService.service.model.SentimentModelService;
import com.tvey.DataAnalysisService.service.result.interfaces.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.stereotype.Service;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoAnalyzer implements TextAnalyzer<YtContentDTO, YtVideoAnalysisDTO> {

    private final SentimentModelService modelService;

    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    @Override
    public YtVideoAnalysisDTO analyzeEntity(ApiDTO<YtContentDTO> apiDTO) throws IOException {


        List<YtContentDTO> comments = Objects.requireNonNull(apiDTO).getContent();

        DoccatModel model;
        String fileName = "analysis-service/src/main/resources/models/en-sentiment.bin";
        File modelFile = new File(fileName);


        if (modelFile.exists()) {
            model = modelService.loadModel(fileName);
        } else {
            model = unwrapModel(modelService.trainModel("twitter_training.csv"));
            modelService.saveModel(model, fileName);
        }


        DocumentCategorizerME categorizer = new DocumentCategorizerME(model);


        List<CommentAnalysisResult> commentAnalysisResults = new ArrayList<>(apiDTO.getContentSize());

        int positive = 0;
        int negative = 0;
        int neutral = 0;
        int irrelevant = 0;

        String videoId = apiDTO.getId();

         VideoAnalysisResult analysisResult =  VideoAnalysisResult.builder()
                .videoId(videoId)
                .build();

        for (YtContentDTO comment : Objects.requireNonNull(comments)) {

            String[] tokens = tokenizer.tokenize(comment.getContent());

            double[] probabilities = categorizer.categorize(tokens);
            String category = categorizer.getBestCategory(probabilities);

            switch (category) {
                case "Positive" -> positive++;
                case "Negative" -> negative++;
                case "Neutral" -> neutral++;
                case "Irrelevant" -> irrelevant++;
            }

            commentAnalysisResults.add(
                    CommentAnalysisResult.builder()
                            .videoAnalysisResult(analysisResult)
                            .content(comment.getContent())
                            .category(category)
                            .build()
            );


        }

        analysisResult.setPositive(positive);
        analysisResult.setNegative(negative);
        analysisResult.setNeutral(neutral);
        analysisResult.setIrrelevant(irrelevant);

        return YtVideoAnalysisDTO.builder()
                .videoAnalysisResult(analysisResult)
                .commentAnalysisResults(commentAnalysisResults)
                .dataSource(DataSource.YOUTUBE)
                .build();


    }

    private DoccatModel unwrapModel(Optional<DoccatModel> model) {
        if (model.isPresent()) {
            return model.get();
        } else {
            throw new IllegalStateException("Trained model is null");
        }
    }

}