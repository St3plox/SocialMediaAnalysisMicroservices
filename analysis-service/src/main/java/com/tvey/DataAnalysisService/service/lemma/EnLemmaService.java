package com.tvey.DataAnalysisService.service.lemma;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class EnLemmaService implements LemmaService{

    InputStream dictLemmatizer = getClass()
            .getResourceAsStream("/models/en-lemmatizer.dict");
    DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(
            dictLemmatizer);

    public EnLemmaService() throws IOException {
    }


    @Override
    public String[] lemmatize(String[] tokens, String[] tags) {
        return lemmatizer.lemmatize(tokens, tags);
    }
}
