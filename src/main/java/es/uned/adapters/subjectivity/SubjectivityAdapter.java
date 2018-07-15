package es.uned.adapters.subjectivity;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface SubjectivityAdapter {

    String MODELS_DIR = "/models/subjectivity";
    ClassifierType adapterType = ClassifierType.OPINION;

    String get_adapter_path();

    void analyze(Corpus corpus, Analysis analysis);

    void trainModel(String modelLocation, Map<Enum, List<String>> datasets);

    void createModel(String modelLocation, Map<String,String> options, Map<Enum, List<String>> datasets);

}
