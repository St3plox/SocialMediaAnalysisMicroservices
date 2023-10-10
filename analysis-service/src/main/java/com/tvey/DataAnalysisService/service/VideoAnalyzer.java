package com.tvey.DataAnalysisService.service;

import com.tvey.DataAnalysisService.entity.CommentAnalysisResult;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import com.tvey.DataAnalysisService.service.model.SentimentModelService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
public class VideoAnalyzer implements TextAnalyzer {

    private final WebClient.Builder webClient;

    private final SentimentModelService modelService;

    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    @Override
    public VideoAnalysisResult analyzeEntity(String url, long maxComments) throws IOException {

        ParameterizedTypeReference<ApiDTO<YtContentDTO>> typeReference =
                new ParameterizedTypeReference<>() {
                };

        ApiDTO<YtContentDTO> apiDTO = webClient
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build()
                .get()
                .uri("http://data-processing-service/api/data/" +
                        "yt?url=" + url + "&" + "maxComments=" + maxComments)
                .retrieve()
                .bodyToMono(typeReference)
                .block();

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


        List<CommentAnalysisResult> commentAnalysisResults = new ArrayList<>((int) maxComments);

        int positive = 0;
        int negative = 0;
        int neutral = 0;
        int irrelevant = 0;


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

            commentAnalysisResults.add(new CommentAnalysisResult(
                    comment.getId(),
                    url,
                    comment.getAuthorDisplayName(),
                    comment.getContent(),
                    comment.getPublishedAt(),
                    category
            ));
        }

        int commentsAmount = apiDTO.getContentSize();
        String videoId = apiDTO.getId();

        return new VideoAnalysisResult(
                1L,
                videoId,
                (double) positive * 100 / commentsAmount,
                (double) negative * 100 / commentsAmount,
                (double) neutral * 100 / commentsAmount,
                (double) irrelevant * 100 / commentsAmount,
                commentsAmount,
                commentAnalysisResults
        );
    }

    private DoccatModel unwrapModel(Optional<DoccatModel> model) {
        if (model.isPresent()) {
            return model.get();
        } else {
            throw new IllegalStateException("Trained model is null");
        }
    }
}