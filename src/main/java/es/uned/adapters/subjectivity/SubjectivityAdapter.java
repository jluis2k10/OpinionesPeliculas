package es.uned.adapters.subjectivity;

import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface SubjectivityAdapter {

    String MODELS_DIR = "/models/subjectivity";

    void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options);

    void trainModel(String modelLocation, List<String> positives, List<String> negatives);

    void createModel();

}
