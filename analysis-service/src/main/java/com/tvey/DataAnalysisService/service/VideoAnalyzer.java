package com.tvey.DataAnalysisService.service;

import com.tvey.DataAnalysisService.dto.YtTransferObject;
import com.tvey.DataAnalysisService.entity.CommentSentiment;
import com.tvey.DataAnalysisService.entity.VideoAnalysisResult;
import com.tvey.DataAnalysisService.service.lemma.LemmaService;
import com.tvey.DataAnalysisService.service.model.SentimentModelService;
import com.tvey.DataAnalysisService.service.pos.PosService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VideoAnalyzer implements TextAnalyzer {

    private final WebClient.Builder webClient;

    private final ResourceLoader resourceLoader;

    private final SentimentModelService modelService;

    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    private final LemmaService lemmaService;

    private final PosService posService;

    @Override
    public VideoAnalysisResult analyzeEntity(String url, long maxComments) throws IOException, ClassNotFoundException {

        ParameterizedTypeReference<List<YtTransferObject>> typeReference =
                new ParameterizedTypeReference<>() {
                };

        List<YtTransferObject> comments = webClient
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

        Resource resource = resourceLoader.getResource("classpath:models/en-sentiment.bin");

        DoccatModel model;


        if (resource.exists()) {
            model = modelService.loadModel("classpath:models/en-sentiment.bin");
        } else {
            model = unwrapModel(modelService.trainModel("twitter_training.csv"));
            /*           modelService.saveModel(model, "en-sentiment.bin");*/
        }


        DocumentCategorizerME categorizer = new DocumentCategorizerME(model);


        assert comments != null;

        List<CommentSentiment> commentSentiments = new ArrayList<>((int) maxComments);

        int positive = 0;
        int negative = 0;
        int neutral = 0;
        int irrelevant = 0;

        for (YtTransferObject comment : comments) {
            String[] lemmatizedTokens;
            String[] tokens = tokenizer.tokenize(comment.getContent());
            String[] tags = posService.tag(tokens);
            lemmatizedTokens = lemmaService.lemmatize(tokens, tags);

            double[] probabilities = categorizer.categorize(tokens);
            String category = categorizer.getBestCategory(probabilities);

            switch (category) {
                case "Positive" -> positive++;
                case "Negative" -> negative++;
                case "Neutral" -> neutral++;
                case "Irrelevant" -> irrelevant++;
            }

            commentSentiments.add(new CommentSentiment(
                    comment.getId(),
                    url,
                    comment.getAuthorDisplayName(),
                    comment.getContent(),
                    comment.getPublishedAt(),
                    category
            ));
        }

        int commentsAmount = comments.size();

        return new VideoAnalysisResult(
                "1",
                url,
                (double) positive * 100 / commentsAmount,
                (double) negative * 100 / commentsAmount,
                (double) neutral * 100 / commentsAmount,
                (double) irrelevant * 100 / commentsAmount,
                commentsAmount,
                commentSentiments
        );
    }

    private static DoccatModel unwrapModel(Optional<DoccatModel> model) {
        if (model.isPresent()) {
            return model.get();
        } else {
            throw new IllegalStateException("Trained model is null");
        }
    }
}