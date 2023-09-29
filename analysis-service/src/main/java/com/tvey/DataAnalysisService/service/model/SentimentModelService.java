package com.tvey.DataAnalysisService.service.model;

import lombok.RequiredArgsConstructor;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.TrainingParameters;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SentimentModelService implements ModelService<DoccatModel> {

    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    @Override
    public Optional<DoccatModel> trainModel(String trainingDataFile) throws IOException {


        List<String[]> trainingData = new ArrayList<>();

        InputStream inputStream = getClass().getResourceAsStream("/twitter_training.csv");

        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                trainingData.add(parts);
            }
        }
        reader.close();

        List<DocumentSample> samples = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (String[] data : trainingData) {

            int i = data.length - 1;
            while (i >= 3) {
                stringBuilder.append(data[i]);
                i--;
            }

            String text = stringBuilder.toString();
            String category = data[2];

            stringBuilder.setLength(0);

            samples.add(new DocumentSample(category, tokenizer.tokenize(text)));
        }

        DoccatFactory factory = new DoccatFactory();
        TrainingParameters params = new TrainingParameters();


        params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
        params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EventTrainer");
        params.put(TrainingParameters.ITERATIONS_PARAM, 10000);
        params.put(TrainingParameters.CUTOFF_PARAM, 2);

        return Optional.of(DocumentCategorizerME.train("en", () -> {
            if (!samples.isEmpty()) {
                return samples.remove(0);
            }
            return null;
        }, params, factory));

    }

    @Override
    public DoccatModel loadModel(String filePath) {
        try (InputStream modelIn = new FileInputStream(filePath)) {
            return new DoccatModel(modelIn);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveModel(DoccatModel model, String filePath) {

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
            model.serialize(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
