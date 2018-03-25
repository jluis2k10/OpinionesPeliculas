package es.uned.adapters.sentiment;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface SentimentAdapter {

    String MODELS_DIR = "/models/sentiment";
    ClassifierType adapterType = ClassifierType.POLARITY;

    String get_adapter_path();

    void analyze(Corpus corpus, Analysis analysis);

    void trainModel(String modelLocation, List<String> positives, List<String> negatives);

    void createModel(String modelLocation, Map<String,String> options, List<String> positives, List<String> negatives);

}
