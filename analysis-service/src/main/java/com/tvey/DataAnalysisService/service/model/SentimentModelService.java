package com.tvey.DataAnalysisService.service.model;

import com.tvey.DataAnalysisService.service.lemma.LemmaService;
import com.tvey.DataAnalysisService.service.pos.PosService;
import com.tvey.DataAnalysisService.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.TrainingParameters;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SentimentModelService implements ModelService<DoccatModel>{

    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    private final ResourceLoader resourceLoader;

    private final PosService posService;

    private final LemmaService lemmaService;


    @Override
    public Optional<DoccatModel> trainModel(String trainingDataFile) throws IOException {


        List<String[]> trainingData = new ArrayList<>();

        InputStream inputStream = getClass().getResourceAsStream("/twitter_training.csv");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                trainingData.add(parts);
            }
        }
        reader.close();

        List<DocumentSample> samples = new ArrayList<>();

        for (String[] data : trainingData) {
            String text = data[3];
            String category = data[2];


            String[] tokens = tokenizer.tokenize(text);
            String[] tags = posService.tag(tokens);


            // Tokenize the text
            samples.add(new DocumentSample(category, tokens));
        }

        DoccatFactory factory = new DoccatFactory();
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 1);
        params.put(TrainingParameters.ITERATIONS_PARAM, 5000);

        return Optional.of(DocumentCategorizerME.train("en", () -> {
            if (!samples.isEmpty()) {
                return samples.remove(0);
            }
            return null;
        }, params, factory));

    }

    @Override
    public DoccatModel loadModel(String resourcePath) throws IOException, ClassNotFoundException {

        try (InputStream is = resourceLoader.getResource(resourcePath).getInputStream();
             ObjectInputStream ois = new ObjectInputStream(is)) {
            return (DoccatModel) ois.readObject();
        }
    }

    @Override
    public void saveModel(DoccatModel model, String fileName) throws IOException {
        // Get the resource (file) path under src/main/resources/models
        Resource resource = resourceLoader.getResource("classpath:/models/");

        // Get the absolute file path
        Path filePath = resource.getFile().toPath();

        // Create directories if they don't exist
        Files.createDirectories(filePath.getParent());

        // Serialize and save the model to the resource file
        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(model);
            oos.close(); // Make sure to close the ObjectOutputStream
        }
    }
}
