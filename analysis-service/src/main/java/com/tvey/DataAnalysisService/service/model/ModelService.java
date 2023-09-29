package com.tvey.DataAnalysisService.service.model;

import opennlp.tools.util.model.BaseModel;

import java.io.IOException;
import java.util.Optional;

public interface ModelService<T extends BaseModel> {

    Optional<T> trainModel(String trainingDataFile) throws IOException;

    T loadModel( String filePath) throws IOException, ClassNotFoundException;

    void saveModel(T model, String filePath) throws IOException;
}