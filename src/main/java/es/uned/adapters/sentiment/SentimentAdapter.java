package es.uned.adapters.sentiment;

import es.uned.adapters.AdapterType;
import es.uned.entities.Search;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface SentimentAdapter {

    String MODELS_DIR = "/models/sentiment";
    AdapterType adapterType = AdapterType.SENTIMENT;

    String get_adapter_path();

    void analyze(Search search);

    void trainModel(String modelLocation, List<String> positives, List<String> negatives);

    void createModel(String modelLocation, Map<String,String> options, List<String> positives, List<String> negatives);

}
