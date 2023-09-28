package com.tvey.DataAnalysisService.service.lemma;

public interface LemmaService {

    String[] lemmatize(String[] tokens, String[] tags);
}
